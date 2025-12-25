package dss2526.domain.entity;

public class LinhaProduto {
    private int id;
    private int idIngrediente;
    private Double quantidade;

    public LinhaProduto() {}

    public LinhaProduto(int idIngrediente, Double quantidade) {
        this.idIngrediente = idIngrediente;
        this.quantidade = quantidade;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdIngrediente() { return idIngrediente; }
    public void setIdIngrediente(int idIngrediente) { this.idIngrediente = idIngrediente; }
    
    public Double getQuantidade() { return quantidade; }
    public void setQuantidade(Double quantidade) { this.quantidade = quantidade; }
}