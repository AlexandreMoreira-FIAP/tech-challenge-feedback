package br.com.fiap.core.domain;

public class FeedbackDTO {
    public Long id;
    public String descricao;
    public Integer nota;
    public boolean urgente;

    @Override
    public String toString() {
        return "FeedbackDTO{id=" + id + ", nota=" + nota + ", urgente=" + urgente + "}";
    }
}