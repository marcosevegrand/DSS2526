package dss2526.domain.entity;

public class LinhaStock {
    private int id;
    private int idIngrediente;
    private double quantidade;

    // Construtores

    public LinhaStock() {}

    public LinhaStock(int idIngrediente, double quantidade) {
        this.idIngrediente = idIngrediente;
        this.quantidade = quantidade;
    }

    // Getters e Setters

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdIngrediente() { return idIngrediente; }
    public void setIdIngrediente(int idIngrediente) { this.idIngrediente = idIngrediente; }
    
    public double getQuantidade() { return quantidade; }
    public void setQuantidade(double quantidade) { this.quantidade = quantidade; }

}
