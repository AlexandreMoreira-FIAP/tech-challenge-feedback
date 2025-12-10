package br.com.fiap.adapter.driven.infra.database;

import br.com.fiap.adapter.driven.infra.database.entity.FeedbackEntity;
import br.com.fiap.core.domain.model.Feedback;
import br.com.fiap.core.usecase.port.FeedbackRepositoryPort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class FeedbackRepositoryAdapter implements FeedbackRepositoryPort {

    @Override
    @Transactional
    public Feedback salvar(Feedback feedback) {
        FeedbackEntity entity = new FeedbackEntity(feedback.getDescricao(), feedback.getNota());

        entity.persist();

        return feedback;
    }

    @Override
    public List<Feedback> listarTodos() {
        return FeedbackEntity.listAll().stream()
                .map(obj -> (FeedbackEntity) obj)
                .map(entity -> new Feedback(entity.descricao, entity.nota))
                .collect(Collectors.toList());
    }
}