package dss2526.domain.entity;

import java.util.ArrayList;
import java.util.List;

public class Stock {
    private Integer id;
    private List<LinhaStock> ingredientes;
    private int restauranteId;

    // Construtores

    public Stock() {
        this.ingredientes = new ArrayList<>();
    }

    public Stock(int restauranteId) {
        this();
        this.restauranteId = restauranteId;
    }

    public boolean temStockDisponivel(int idIngrediente, float quantidadeNecessaria) {
        return ingredientes.stream()
                .filter(l -> l.getIngrediente().getId() == idIngrediente)
                .anyMatch(l -> l.getQuantidade() >= quantidadeNecessaria);
    }

    // Getters e Setters

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public List<LinhaStock> getIngredientes() { return ingredientes; }
    public void setIngredientes(List<LinhaStock> ingredientes) { this.ingredientes = ingredientes; }

    public int  getRestauranteId() { return restauranteId; }
    public void setRestauranteId(int restauranteId) { this.restauranteId = restauranteId; }
}