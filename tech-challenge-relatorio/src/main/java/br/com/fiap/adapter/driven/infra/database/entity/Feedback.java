package br.com.fiap.adapter.driven.infra.database.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "feedbacks")
public class Feedback extends PanacheEntity {

    public String descricao;

    public Integer nota;

    public LocalDateTime dataCriacao;

    public Feedback() {}

    public boolean isUrgente() {
        return this.nota != null && this.nota <= 5;
    }
}