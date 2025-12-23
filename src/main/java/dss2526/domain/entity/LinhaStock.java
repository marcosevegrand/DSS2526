package dss2526.domain.entity;

public class LinhaStock {
    private Integer id;
    private Ingrediente ingrediente;
    private Double quantidade;

    // Construtores

    public LinhaStock() {}

    public LinhaStock(Ingrediente ingrediente, Double quantidade) {
        this.ingrediente = ingrediente;
        this.quantidade = quantidade;
    }

    // Getters e Setters

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Ingrediente getIngrediente() { return ingrediente; }
    public void setIngrediente(Ingrediente ingrediente) { this.ingrediente = ingrediente; }
    
    public Double getQuantidade() { return quantidade; }
    public void setQuantidade(Double quantidade) { this.quantidade = quantidade; }

}
