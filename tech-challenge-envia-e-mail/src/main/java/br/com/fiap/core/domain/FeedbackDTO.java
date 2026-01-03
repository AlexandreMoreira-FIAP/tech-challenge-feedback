package br.com.fiap.core.domain;

import java.time.LocalDateTime;

public class FeedbackDTO {
    public Long id;
    public String descricao;
    public Integer nota;
    public boolean urgente;
    public LocalDateTime dataCriacao;

    @Override
    public String toString() {
        return "FeedbackDTO{" +
                "id=" + id +
                ", nota=" + nota +
                ", urgente=" + urgente +
                ", dataCriacao=" + dataCriacao +
                '}';
    }
}