package dss2526.domain.entity;

import java.util.List;

public class Stock {
    private Integer id;
    private List<LinhaStock> ingredientes;

    // Construtores

    public Stock() {}

    public Stock(List<LinhaStock> ingredientes) {
        this.ingredientes = ingredientes;
    }

    // Lógica Simples


    // Getters e Setters

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public List<LinhaStock> getIngredientes() { return ingredientes; }
    public void setIngredientes(List<LinhaStock> ingredientes) { this.ingredientes = ingredientes; }
    
}