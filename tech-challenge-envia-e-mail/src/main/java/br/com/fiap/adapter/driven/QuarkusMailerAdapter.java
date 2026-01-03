package br.com.fiap.adapter.driven;

import br.com.fiap.core.port.NotificadorEmailPort;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

@ApplicationScoped
public class QuarkusMailerAdapter implements NotificadorEmailPort {

    @Inject
    Mailer mailer;

    @Inject
    Logger LOG;


    @ConfigProperty(name = "quarkus.mailer.from")
    String remetente;

    @Override
    public void enviarEmail(String destinatario, String assunto, String mensagemHtml) {
        try {

            Mail email = Mail.withHtml(destinatario, assunto, mensagemHtml)
                    .setFrom(remetente);

            mailer.send(email);

            LOG.info("📧 [Adapter] Sucesso! E-mail enviado de " + remetente + " para " + destinatario);

        } catch (Exception e) {
            LOG.error("❌ [Adapter] Erro crítico ao enviar e-mail: " + e.getMessage(), e);
        }
    }
}