package br.com.fiap.core.usecase;

import br.com.fiap.core.domain.FeedbackDTO;
import br.com.fiap.core.port.NotificadorEmailPort;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class ProcessarFeedbackUseCase {

    private final NotificadorEmailPort notificador;

    public ProcessarFeedbackUseCase(NotificadorEmailPort notificador) {
        this.notificador = notificador;
    }

    // AGORA RECEBE UMA LISTA!
    public void executar(List<FeedbackDTO> feedbacks) {
        if (feedbacks.isEmpty()) return;

        int total = feedbacks.size();

        String assunto = "RESUMO: " + total + " Feedbacks Urgentes Recebidos";

        StringBuilder html = new StringBuilder();
        html.append("<html><body>");
        html.append("<h2>🚨 Relatório de Feedbacks Urgentes</h2>");
        html.append("<p>Foram encontrados <strong>").append(total).append("</strong> novos feedbacks críticos na fila.</p>");

        html.append("<table border='1' cellpadding='5' style='border-collapse: collapse; width: 100%;'>");
        html.append("<tr style='background-color: #f2f2f2;'><th>ID</th><th>Nota</th><th>Descrição</th></tr>");

        for (FeedbackDTO f : feedbacks) {
            html.append("<tr>");
            html.append("<td>").append(f.id).append("</td>");
            html.append("<td style='color: red; font-weight: bold;'>").append(f.nota).append("</td>");
            html.append("<td>").append(f.descricao).append("</td>");
            html.append("</tr>");
        }

        html.append("</table>");
        html.append("<br><p><i>Enviado automaticamente pelo Worker de Processamento.</i></p>");
        html.append("</body></html>");

        notificador.enviarEmail("admin@fiap.com.br", assunto, html.toString());
    }
}