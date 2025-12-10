package br.com.fiap.core.usecase.port;

import br.com.fiap.core.domain.model.Feedback;
import java.util.List;

public interface FeedbackRepositoryPort {
    Feedback salvar(Feedback feedback);
    List<Feedback> listarTodos();
}