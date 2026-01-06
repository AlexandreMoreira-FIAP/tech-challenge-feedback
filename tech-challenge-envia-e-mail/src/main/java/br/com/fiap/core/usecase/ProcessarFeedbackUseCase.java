package br.com.fiap.core.usecase;

import br.com.fiap.core.domain.FeedbackDTO;
import br.com.fiap.core.port.NotificadorEmailPort;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import java.time.format.DateTimeFormatter;
import java.util.List;

@ApplicationScoped
public class ProcessarFeedbackUseCase {

    private final NotificadorEmailPort notificador;

    @ConfigProperty(name = "email.destinatario.admin", defaultValue = "alexandre.dellaestudos@gmail.com")
    String emailDestino;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public ProcessarFeedbackUseCase(NotificadorEmailPort notificador) {
        this.notificador = notificador;
    }

    public void executar(List<FeedbackDTO> feedbacks) {
        if (feedbacks.isEmpty()) return;

        int total = feedbacks.size();
        String assunto = "RESUMO: " + total + " Feedbacks Urgentes Recebidos";

        StringBuilder html = new StringBuilder();
        html.append("<html><body>");
        html.append("<h2>🚨 Relatório de Feedbacks Urgentes</h2>");
        html.append("<p>Foram encontrados <strong>").append(total).append("</strong> novos feedbacks críticos na fila.</p>");

        html.append("<table border='1' cellpadding='5' style='border-collapse: collapse; width: 100%; text-align: left;'>");
        html.append("<tr style='background-color: #f2f2f2;'>");
        html.append("<th>ID</th>");
        html.append("<th>Data</th>");
        html.append("<th>Nota</th>");
        html.append("<th>Descrição</th>");
        html.append("</tr>");

        for (FeedbackDTO f : feedbacks) {
            String dataFormatada = (f.dataCriacao != null) ? f.dataCriacao.format(FORMATTER) : "N/A";

            html.append("<tr>");
            html.append("<td>").append(f.id).append("</td>");
            html.append("<td>").append(dataFormatada).append("</td>");
            html.append("<td style='color: red; font-weight: bold;'>").append(f.nota).append("</td>");
            html.append("<td>").append(f.descricao).append("</td>");
            html.append("</tr>");
        }

        html.append("</table>");
        html.append("<br><p><i>Enviado automaticamente, por gentileza fazer o tratamento.</i></p>");
        html.append("</body></html>");

        notificador.enviarEmail(emailDestino, assunto, html.toString());
    }
}