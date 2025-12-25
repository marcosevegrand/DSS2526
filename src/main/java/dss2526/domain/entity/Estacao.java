package dss2526.domain.entity;

import dss2526.domain.enumeration.Trabalho;

public class Estacao {
    private int id;
    private int restauranteId;
    private Trabalho trabalho;

    // Construtores

    public Estacao() {}

    // Getters e Setters

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getRestauranteId() { return restauranteId; }
    public void setRestauranteId(int restauranteId) { this.restauranteId = restauranteId; }

    public Trabalho getTrabalho() { return trabalho; }
    public void setTrabalho(Trabalho trabalho) { this.trabalho = trabalho; }

    @Override
    public String toString() {
        return "Estacao{" +
                "id=" + id +
                ", restauranteId=" + restauranteId +
                ", trabalho=" + trabalho +
                '}';
    }
}