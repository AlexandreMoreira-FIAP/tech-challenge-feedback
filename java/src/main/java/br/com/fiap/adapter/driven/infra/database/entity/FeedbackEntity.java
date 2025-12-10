package br.com.fiap.adapter.driven.infra.database.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "feedbacks")
public class FeedbackEntity extends PanacheEntity {

    public String descricao;
    public Integer nota;

    public FeedbackEntity() {}

    public FeedbackEntity(String descricao, Integer nota) {
        this.descricao = descricao;
        this.nota = nota;
    }
}