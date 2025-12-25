package dss2526.domain.entity;

import java.util.ArrayList;
import java.util.List;

public class Stock {
    private Integer id;
    private List<LinhaStock> ingredientes;
    private Integer restauranteId;
    private Restaurante restaurante;

    // Construtores

    public Stock() {
        this.ingredientes = new ArrayList<>();
    }

    public Stock(Integer restauranteId) {
        this();
        this.restauranteId = restauranteId;
    }

    public boolean temStockDisponivel(int idIngrediente, float quantidadeNecessaria) {
        return ingredientes.stream()
                .filter(l -> l.getIngrediente().getId() == idIngrediente)
                .anyMatch(l -> l.getQuantidade() >= quantidadeNecessaria);
    }

    // Getters e Setters

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public List<LinhaStock> getIngredientes() { return ingredientes; }
    public void setIngredientes(List<LinhaStock> ingredientes) { this.ingredientes = ingredientes; }

    public Restaurante getRestaurante() { return restaurante; }
    public void setRestaurante(Restaurante restaurante) { this.restaurante = restaurante; }
    
    public Integer getRestauranteId() { return restauranteId; }
    public void setRestauranteId(Integer restauranteId) { this.restauranteId = restauranteId; }
}