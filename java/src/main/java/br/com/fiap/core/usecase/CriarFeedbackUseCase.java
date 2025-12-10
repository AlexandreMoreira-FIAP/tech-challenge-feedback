package br.com.fiap.core.usecase;

import br.com.fiap.core.domain.model.Feedback;
import br.com.fiap.core.usecase.port.FeedbackRepositoryPort;

public class CriarFeedbackUseCase {

    private final FeedbackRepositoryPort repository;

    public CriarFeedbackUseCase(FeedbackRepositoryPort repository) {
        this.repository = repository;
    }

    public Feedback executar(Feedback feedback) {

        Feedback salvo = repository.salvar(feedback);

        // 2. (Futuro) Aqui entra a lógica: Se nota < 5 -> Enviar para Fila Azure

        return salvo;
    }
}