package br.com.fiap.adapter.driven.infra.email;

import br.com.fiap.core.usecase.port.NotificacaoPort;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import io.quarkus.logging.Log; // <--- O Importante está aqui
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class MailerAdapter implements NotificacaoPort {

    @Inject
    Mailer mailer;

    @ConfigProperty(name = "quarkus.mailer.mock", defaultValue = "false")
    boolean isMock;

    @Override
    public void enviarRelatorio(String destinatario, String assunto, String mensagemHtml) {
        try {
            mailer.send(Mail.withHtml(destinatario, assunto, mensagemHtml));

            Log.info("✅ [EMAIL] Sucesso! Relatório enviado para: " + destinatario);

            // SE estivermos em modo Mock (Azure/Dev), logamos o HTML para prova no vídeo
            if (isMock) {
                Log.info("📝 [CONTEÚDO DO EMAIL - MOCK START] --------------------------------");
                Log.info(mensagemHtml);
                Log.info("📝 [CONTEÚDO DO EMAIL - MOCK END] ----------------------------------");
            }

        } catch (Exception e) {
            Log.error("❌ [EMAIL] Falha ao enviar relatório: " + e.getMessage(), e);
        }
    }
}