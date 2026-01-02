package br.com.fiap.adapter.driven.infra.database;

import br.com.fiap.adapter.driven.infra.database.entity.Feedback;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class FeedbackRepository {

    public List<Feedback> buscarUltimos7Dias() {
        LocalDateTime dataLimite = LocalDateTime.now().minusDays(7);

        return Feedback.list("dataCriacao >= ?1", dataLimite);
    }
}