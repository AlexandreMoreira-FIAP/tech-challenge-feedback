package br.com.fiap.core.domain.model;

public class Feedback {

    private String descricao;
    private Integer nota;

    public Feedback(String descricao, Integer nota) {
        validarNota(nota);
        this.descricao = descricao;
        this.nota = nota;
    }

    private void validarNota(Integer nota) {
        if (nota == null) {
            throw new IllegalArgumentException("A nota é obrigatória.");
        }
        if (nota < 0 || nota > 10) {
            throw new IllegalArgumentException("A nota deve ser entre 0 e 10.");
        }
    }

    public String getDescricao() { return descricao; }
    public Integer getNota() { return nota; }
}