package br.com.fiap.core.usecase.port;

import br.com.fiap.core.domain.model.Feedback;

public interface NotificadorPort {
    void notificarUrgencia(Feedback feedback);
}