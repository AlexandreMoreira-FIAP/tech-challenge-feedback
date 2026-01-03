package br.com.fiap.adapter.driven.infra.email;

import br.com.fiap.core.usecase.port.NotificacaoPort;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class MailerAdapter implements NotificacaoPort {

    @Inject
    Mailer mailer;

    @ConfigProperty(name = "quarkus.mailer.from")
    String remetente;

    @ConfigProperty(name = "quarkus.mailer.mock", defaultValue = "false")
    boolean isMock;

    @Override
    public void enviarRelatorio(String destinatario, String assunto, String mensagemHtml) {
        try {

            Mail email = Mail.withHtml(destinatario, assunto, mensagemHtml)
                    .setFrom(remetente);

            mailer.send(email);

            Log.info("✅ [EMAIL] Sucesso! Relatório enviado de " + remetente + " para " + destinatario);

            if (isMock) {
                Log.info("📝 [CONTEÚDO MOCK] --------------------------------");
                Log.info(mensagemHtml);
                Log.info("----------------------------------------------------");
            }

        } catch (Exception e) {
            Log.error("❌ [EMAIL] Falha crítica ao enviar relatório: " + e.getMessage(), e);
        }
    }
}