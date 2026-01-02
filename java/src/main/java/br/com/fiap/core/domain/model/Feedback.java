package br.com.fiap.core.domain.model;

import java.time.LocalDateTime; // Importação correta

public class Feedback {

    private Long id;
    private String descricao;
    private Integer nota;
    private LocalDateTime dataCriacao;

    public Feedback(String descricao, Integer nota) {
        validarNota(nota);
        this.descricao = descricao;
        this.nota = nota;
        this.dataCriacao = LocalDateTime.now();
    }

    public Feedback(Long id, String descricao, Integer nota, LocalDateTime dataCriacao) {
        this(descricao, nota);
        this.id = id;
        this.dataCriacao = dataCriacao;
    }

    public Feedback() {}

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

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }
}