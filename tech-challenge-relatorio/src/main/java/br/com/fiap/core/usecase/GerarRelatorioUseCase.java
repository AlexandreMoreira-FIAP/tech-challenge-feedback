package br.com.fiap.core.usecase;

import br.com.fiap.adapter.driven.infra.database.FeedbackRepository;
import br.com.fiap.adapter.driven.infra.database.entity.Feedback;
import br.com.fiap.core.usecase.port.NotificacaoPort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
public class GerarRelatorioUseCase {

    @Inject
    FeedbackRepository repository;

    @Inject
    NotificacaoPort notificacaoPort;

    @Transactional
    public void executar() {
        List<Feedback> feedbacks = repository.buscarUltimos7Dias();

        if (feedbacks.isEmpty()) {
            System.out.println("⚠️ Nenhum feedback encontrado nos últimos 7 dias.");
            return;
        }

        long totalUrgentes = feedbacks.stream().filter(Feedback::isUrgente).count();
        long totalNaoUrgentes = feedbacks.size() - totalUrgentes;

        Map<LocalDate, Long> feedbacksPorDia = feedbacks.stream()
                .collect(Collectors.groupingBy(
                        f -> f.dataCriacao.toLocalDate(),
                        Collectors.counting()
                ));

        StringBuilder html = new StringBuilder();
        DateTimeFormatter dataFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        DateTimeFormatter diaFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        html.append("<html><body style='font-family: Arial, sans-serif;'>");

        html.append("<h2 style='color: #2c3e50;'>📊 Relatório Semanal de Feedbacks</h2>");
        html.append("<div style='background-color: #f8f9fa; padding: 15px; border-radius: 5px; margin-bottom: 20px;'>");
        html.append("<p><strong>Total na semana:</strong> ").append(feedbacks.size()).append("</p>");
        html.append("<p style='color: #c0392b;'><strong>🔴 Urgentes:</strong> ").append(totalUrgentes).append("</p>");
        html.append("<p style='color: #27ae60;'><strong>🟢 Normais:</strong> ").append(totalNaoUrgentes).append("</p>");
        html.append("</div>");

        html.append("<h3>📅 Volume por Dia</h3>");
        html.append("<ul>");
        feedbacksPorDia.forEach((data, qtd) ->
                html.append("<li><strong>").append(data.format(diaFmt)).append(":</strong> ").append(qtd).append(" feedbacks</li>")
        );
        html.append("</ul>");

        html.append("<h3>📝 Detalhamento dos Feedbacks</h3>");
        html.append("<table border='1' cellpadding='8' style='border-collapse: collapse; width: 100%; border: 1px solid #ddd;'>");
        html.append("<tr style='background-color: #2c3e50; color: white;'><th>Data</th><th>Status</th><th>Nota</th><th>Descrição</th></tr>");

        for (Feedback f : feedbacks) {
            String corNota = f.isUrgente() ? "#c0392b" : "#27ae60"; // Vermelho se urgente, Verde se normal
            String statusIcon = f.isUrgente() ? "🔴 URGENTE" : "🟢 Normal";
            String dataFormatada = f.dataCriacao != null ? f.dataCriacao.format(dataFmt) : "N/A";

            html.append("<tr>");
            html.append("<td>").append(dataFormatada).append("</td>");
            html.append("<td style='font-weight: bold; color: ").append(corNota).append(";'>").append(statusIcon).append("</td>");
            html.append("<td style='text-align: center;'>").append(f.nota).append("</td>");
            html.append("<td>").append(f.descricao).append("</td>");
            html.append("</tr>");
        }

        html.append("</table>");
        html.append("<br><p style='font-size: 12px; color: #7f8c8d;'><i>Relatório gerado automaticamente via Azure Functions.</i></p>");
        html.append("</body></html>");

        notificacaoPort.enviarRelatorio("admin@fiap.com.br", "📊 Relatório Semanal Completo", html.toString());
    }
}