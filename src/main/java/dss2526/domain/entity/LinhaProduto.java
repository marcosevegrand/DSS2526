package dss2526.domain.entity;

public class LinhaProduto {
    private int id;
    private Ingrediente ingrediente;
    private Double quantidade;

    public LinhaProduto() {}

    public LinhaProduto(Ingrediente ingrediente, Double quantidade) {
        this.ingrediente = ingrediente;
        this.quantidade = quantidade;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Ingrediente getIngrediente() { return ingrediente; }
    public void setIngrediente(Ingrediente ingrediente) { this.ingrediente = ingrediente; }
    
    public Double getQuantidade() { return quantidade; }
    public void setQuantidade(Double quantidade) { this.quantidade = quantidade; }
}