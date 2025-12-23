package pt.uminho.dss.restaurante.domain.entity;

public class LinhaStock {
    private Long id;
    private Ingrediente ingrediente;
    private Double quantidade;

    // Construtores

    public LinhaStock() {}

    public LinhaStock(Ingrediente ingrediente, Double quantidade) {
        this.ingrediente = ingrediente;
        this.quantidade = quantidade;
    }

    // Getters e Setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Ingrediente getIngrediente() { return ingrediente; }
    public void setIngrediente(Ingrediente ingrediente) { this.ingrediente = ingrediente; }
    
    public Double getQuantidade() { return quantidade; }
    public void setQuantidade(Double quantidade) { this.quantidade = quantidade; }

}
