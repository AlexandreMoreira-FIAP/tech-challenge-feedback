package br.com.fiap.adapter.driven.infra;

import br.com.fiap.core.domain.model.Feedback;
import br.com.fiap.core.usecase.port.NotificadorPort;
import com.azure.storage.queue.QueueClient;
import com.azure.storage.queue.QueueClientBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class AzureQueueAdapter implements NotificadorPort {

    @ConfigProperty(name = "AZURE_STORAGE_CONNECTION_STRING")
    String connectionString;

    @ConfigProperty(name = "QUEUE_NAME")
    String queueName;

    @Override
    public void notificarUrgencia(Feedback feedback) {
        try {
            QueueClient queueClient = new QueueClientBuilder()
                    .connectionString(connectionString)
                    .queueName(queueName)
                    .buildClient();

            queueClient.createIfNotExists();

            ObjectMapper mapper = new ObjectMapper();
            String mensagemJson = mapper.writeValueAsString(feedback);

            // 4. Envia a mensagem! 🚀
            queueClient.sendMessage(mensagemJson);

            System.out.println("[AzureQueueAdapter] Feedback Urgente ID " + feedback.getId() + " enviado para a fila.");

        } catch (Exception e) {
            System.err.println("Erro ao enviar mensagem para a Azure Queue: " + e.getMessage());
            e.printStackTrace();
        }
    }
}