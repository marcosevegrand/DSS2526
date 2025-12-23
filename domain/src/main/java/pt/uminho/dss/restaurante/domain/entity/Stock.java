package pt.uminho.dss.restaurante.domain.entity;

import java.io.Serializable;

import java.util.Map;

public class Stock implements Serializable {
    private Long id; // não sei se deve exister
    private Map<String, Ingrediente> ingredientes;

    // Construtores

    public Stock() {}

    // Getters e Setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    
}