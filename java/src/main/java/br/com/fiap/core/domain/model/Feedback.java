package br.com.fiap.core.domain.model;

public class Feedback {

    private Long id; // Adicionado: Identificador único
    private String descricao;
    private Integer nota;

    public Feedback(String descricao, Integer nota) {
        validarNota(nota);
        this.descricao = descricao;
        this.nota = nota;
    }

    public Feedback(Long id, String descricao, Integer nota) {
        this(descricao, nota); // Reaproveita a validação do construtor de cima
        this.id = id;
    }

    private void validarNota(Integer nota) {
        if (nota == null) {
            throw new IllegalArgumentException("A nota é obrigatória.");
        }
        if (nota < 0 || nota > 10) {
            throw new IllegalArgumentException("A nota deve ser entre 0 e 10.");
        }
    }

    public boolean isUrgente() {
        return this.nota <= 5;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDescricao() { return descricao; }
    public Integer getNota() { return nota; }
}