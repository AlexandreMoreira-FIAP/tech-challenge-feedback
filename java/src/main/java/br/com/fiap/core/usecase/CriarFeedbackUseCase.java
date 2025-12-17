package br.com.fiap.core.usecase;

import br.com.fiap.core.domain.model.Feedback;
import br.com.fiap.core.usecase.port.FeedbackRepositoryPort;
import br.com.fiap.core.usecase.port.NotificadorPort;

public class CriarFeedbackUseCase {

    private final FeedbackRepositoryPort repository;
    private final NotificadorPort notificador;

    public CriarFeedbackUseCase(FeedbackRepositoryPort repository, NotificadorPort notificador) {
        this.repository = repository;
        this.notificador = notificador;
    }

    public Feedback executar(Feedback feedback) {
        Feedback salvo = repository.salvar(feedback);

        if (salvo.isUrgente()) {
            notificador.notificarUrgencia(salvo);
        }

        return salvo;
    }
}