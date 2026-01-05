package br.com.fiap.adapter.driver;

import br.com.fiap.core.domain.FeedbackDTO;
import br.com.fiap.core.usecase.ProcessarFeedbackUseCase;
import com.azure.storage.queue.QueueClient;
import com.azure.storage.queue.QueueClientBuilder;
import com.azure.storage.queue.models.QueueMessageItem;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.quarkus.scheduler.Scheduled;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger; // <--- O IMPORTANTE ESTÁ AQUI

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class FeedbackQueueListener {

    @Inject
    Logger LOG;

    @ConfigProperty(name = "azure.connection.string")
    String connectionString;

    @ConfigProperty(name = "queue.name")
    String queueName;

    @Inject
    ObjectMapper objectMapper;

    @Inject
    ProcessarFeedbackUseCase useCase;

    private QueueClient queueClient;

    @PostConstruct
    public void init() {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        try {
            this.queueClient = new QueueClientBuilder()
                    .connectionString(connectionString)
                    .queueName(queueName)
                    .buildClient();

            this.queueClient.createIfNotExists();
            LOG.info("✅ [INIT] Worker iniciado! Conectado na fila: " + queueName);

        } catch (Exception e) {
            LOG.error("❌ [FATAL] Erro ao conectar na fila no startup!", e);
        }
    }

    @Scheduled(every = "45s")
    public void processarFila() {

        try {
            Iterable<QueueMessageItem> mensagensAzure = queueClient.receiveMessages(32, Duration.ofMinutes(2), Duration.ofSeconds(1), null);

            List<FeedbackDTO> feedbacksParaProcessar = new ArrayList<>();
            List<QueueMessageItem> mensagensParaDeletar = new ArrayList<>();

            for (QueueMessageItem mensagem : mensagensAzure) {
                try {
                    LOG.info("📥 [RECEBIDO] Mensagem ID: " + mensagem.getMessageId());

                    FeedbackDTO feedback = objectMapper.readValue(mensagem.getBody().toString(), FeedbackDTO.class);
                    feedbacksParaProcessar.add(feedback);
                    mensagensParaDeletar.add(mensagem);

                } catch (Exception e) {
                    LOG.error("❌ [ERRO JSON] Falha ao ler msg ID " + mensagem.getMessageId(), e);
                }
            }

            if (!feedbacksParaProcessar.isEmpty()) {
                LOG.info("📦 [PROCESSANDO] Enviando lote de " + feedbacksParaProcessar.size() + " e-mails...");

                useCase.executar(feedbacksParaProcessar);

                for (QueueMessageItem msg : mensagensParaDeletar) {
                    queueClient.deleteMessage(msg.getMessageId(), msg.getPopReceipt());
                }
                LOG.info("🗑️ [LIMPEZA] Lote finalizado e mensagens deletadas da fila.");
            }

        } catch (Exception e) {
            LOG.error("❌ [ERRO GERAL] Falha no loop do Worker", e);
        }
    }
}