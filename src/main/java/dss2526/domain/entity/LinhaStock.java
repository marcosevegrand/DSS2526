package dss2526.domain.entity;

public class LinhaStock {
    private int id;
    private int restauranteId;
    private int ingredienteId;
    private int quantidade;

    // Construtores

    public LinhaStock() {}

    public LinhaStock(int ingredienteId, int quantidade) {
        this.ingredienteId = ingredienteId;
        this.quantidade = quantidade;
    }

    // Getters e Setters

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getRestauranteId() { return restauranteId; }
    public void setRestauranteId(int restauranteId) { this.restauranteId = restauranteId; }

    public int getIngredienteId() { return ingredienteId; }
    public void setIngredienteId(int ingredienteId) { this.ingredienteId = ingredienteId; }
    
    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }

    @Override
    public String toString() {
        return "LinhaStock{" +
                "id=" + id +
                ", restauranteId=" + restauranteId +
                ", ingredienteId=" + ingredienteId +
                ", quantidade=" + quantidade +
                '}';
    }
}