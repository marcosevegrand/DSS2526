package pt.uminho.dss.restaurante.domain.entity;

import java.io.Serializable;

import java.util.List;

public class Stock implements Serializable {
    private Integer id; // não sei se deve exister
    private List<LinhaStock> ingredientes;

    // Construtores

    public Stock() {}

    public Stock(List<LinhaStock> ingredientes) {
        this.ingredientes = ingredientes;
    }

    // Lógica Simples

    // falta como adicionar quantidade ao stock

    // Getters e Setters

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public List<LinhaStock> getIngredientes() { return ingredientes; }
    public void setIngredientes(List<LinhaStock> ingredientes) { this.ingredientes = ingredientes; }
    
}