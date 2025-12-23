package pt.uminho.dss.restaurante.domain.entity;

import java.io.Serializable;

public class Stock implements Serializable {
    private Long id;
    private Ingrediente ingrediente;
    private Double quantidadeDisponivel;
    private Double quantidadeMinima;

    public Stock() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Ingrediente getIngrediente() { return ingrediente; }
    public void setIngrediente(Ingrediente ingrediente) { this.ingrediente = ingrediente; }
    public Double getQuantidadeDisponivel() { return quantidadeDisponivel; }
    public void setQuantidadeDisponivel(Double quantidadeDisponivel) { this.quantidadeDisponivel = quantidadeDisponivel; }
    public Double getQuantidadeMinima() { return quantidadeMinima; }
    public void setQuantidadeMinima(Double quantidadeMinima) { this.quantidadeMinima = quantidadeMinima; }
}