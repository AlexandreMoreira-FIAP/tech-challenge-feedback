package br.com.fiap.adapter.driven;

import br.com.fiap.core.port.NotificadorEmailPort;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class QuarkusMailerAdapter implements NotificadorEmailPort {

    @Inject
    Mailer mailer;

    @Override
    public void enviarEmail(String destinatario, String assunto, String mensagemHtml) {
        mailer.send(Mail.withHtml(destinatario, assunto, mensagemHtml));
        System.out.println("📧 [Adapter] E-mail HTML enviado para " + destinatario);
    }
}