package br.com.fiap.adapter.driven.infra;

import br.com.fiap.core.domain.model.Feedback;
import br.com.fiap.core.usecase.port.NotificadorPort;
import com.azure.storage.queue.QueueClient;
import com.azure.storage.queue.QueueClientBuilder;
import com.azure.storage.queue.models.QueueStorageException; // Importante para pegar o erro específico
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class AzureQueueAdapter implements NotificadorPort {

    @ConfigProperty(name = "azure.connection.string")
    String connectionString;

    @ConfigProperty(name = "queue.name")
    String queueName;

    @Override
    public void notificarUrgencia(Feedback feedback) {
        try {
            QueueClient queueClient = new QueueClientBuilder()
                    .connectionString(connectionString)
                    .queueName(queueName)
                    .buildClient();

            ObjectMapper mapper = new ObjectMapper();
            String mensagemJson = mapper.writeValueAsString(feedback);
            queueClient.sendMessage(mensagemJson);

            System.out.println("✅ [AzureQueueAdapter] Feedback enviado para fila: " + queueName);

        } catch (QueueStorageException e) {
            if (e.getStatusCode() == 404) {
                System.err.println("ERRO: A fila '" + queueName + "' ainda não existe! Verifique se o Worker está rodando.");
            } else {
                System.err.println("Erro da Azure ao enviar mensagem: " + e.getMessage());
            }
        } catch (Exception e) {
            System.err.println("Erro genérico ao notificar urgência: " + e.getMessage());
            e.printStackTrace();
        }
    }
}