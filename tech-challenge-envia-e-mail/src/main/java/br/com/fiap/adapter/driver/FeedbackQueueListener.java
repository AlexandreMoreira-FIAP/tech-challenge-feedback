package br.com.fiap.adapter.driver;

import br.com.fiap.core.domain.FeedbackDTO;
import br.com.fiap.core.usecase.ProcessarFeedbackUseCase;
import com.azure.storage.queue.QueueClient;
import com.azure.storage.queue.QueueClientBuilder;
import com.azure.storage.queue.models.QueueMessageItem;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.scheduler.Scheduled;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class FeedbackQueueListener {

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
        this.queueClient = new QueueClientBuilder()
                .connectionString(connectionString)
                .queueName(queueName)
                .buildClient();
        try {
            this.queueClient.createIfNotExists();
            System.out.println("✅ [Listener] Fila conectada: " + queueName);
        } catch (Exception e) {
            System.err.println("❌ Erro ao conectar na fila: " + e.getMessage());
        }
    }

    // Ajustado para 30 segundos conforme solicitado
    @Scheduled(every = "30s")
    public void processarFila() {
        // Tenta pegar até 32 mensagens (máximo permitido pela Azure num request)
        Iterable<QueueMessageItem> mensagensAzure = queueClient.receiveMessages(32, Duration.ofMinutes(2), Duration.ofSeconds(1), null);

        // Listas auxiliares
        List<FeedbackDTO> feedbacksParaProcessar = new ArrayList<>();
        List<QueueMessageItem> mensagensParaDeletar = new ArrayList<>();

        // 1. Loop de Conversão (Acumula os dados)
        for (QueueMessageItem mensagem : mensagensAzure) {
            try {
                FeedbackDTO feedback = objectMapper.readValue(mensagem.getBody().toString(), FeedbackDTO.class);
                feedbacksParaProcessar.add(feedback);
                mensagensParaDeletar.add(mensagem); // Guarda a msg original para deletar depois
            } catch (Exception e) {
                System.err.println("❌ Erro ao converter mensagem ID " + mensagem.getMessageId());
                // Se der erro de JSON, talvez queira deletar ou mover para uma fila de "Dead Letter"
            }
        }

        // 2. Se tivermos feedbacks válidos, chamamos o UseCase UMA VEZ
        if (!feedbacksParaProcessar.isEmpty()) {
            System.out.println("📦 [Listener] Processando lote de " + feedbacksParaProcessar.size() + " feedbacks.");

            try {
                // Envia o e-mail com a lista
                useCase.executar(feedbacksParaProcessar);

                // 3. Se o e-mail foi, deletamos as mensagens da fila
                for (QueueMessageItem msg : mensagensParaDeletar) {
                    queueClient.deleteMessage(msg.getMessageId(), msg.getPopReceipt());
                }
                System.out.println("🗑️ Lote processado e mensagens removidas.");

            } catch (Exception e) {
                System.err.println("❌ Erro ao enviar e-mail do lote. As mensagens voltarão para a fila em breve.");
                e.printStackTrace();
            }
        }
    }
}