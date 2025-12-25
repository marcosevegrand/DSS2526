package dss2526.domain.entity;

public class LinhaStock {
    private int id;
    private int restauranteId;
    private int ingredienteId;
    private double quantidade;

    // Construtores

    public LinhaStock() {}

    public LinhaStock(int ingredienteId, double quantidade) {
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
    
    public double getQuantidade() { return quantidade; }
    public void setQuantidade(double quantidade) { this.quantidade = quantidade; }

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