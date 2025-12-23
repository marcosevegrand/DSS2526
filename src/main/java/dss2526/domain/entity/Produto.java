package dss2526.domain.entity;

import dss2526.domain.contract.Item;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Produto implements Item, Serializable {
    private Integer id;
    private String nome;
    private BigDecimal preco;
    private boolean disponivel;
    private List<LinhaIngrediente> ingredientes = new ArrayList<>();
    private List<PassoProducao> passos = new ArrayList<>();

    // Construtores

    public Produto() {}

    public Produto(String nome, BigDecimal preco, boolean disponivel,
                   List<LinhaIngrediente> ingredientes, List<PassoProducao> passos) {
        this.nome = nome;
        this.preco = preco;
        this.disponivel = disponivel;
        this.ingredientes = ingredientes;
        this.passos = passos;
    }

    // Lógica simples


    // Getters e Setters

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public BigDecimal getPreco() { return preco; }
    public void setPreco(BigDecimal preco) { this.preco = preco; }

    public boolean isDisponivel() { return disponivel; }
    public void setDisponivel(boolean disponivel) { this.disponivel = disponivel; }
        
    public List<LinhaIngrediente> getIngredientes() { return ingredientes; }
    public void setIngredientes(List<LinhaIngrediente> ingredientes) { this.ingredientes = ingredientes; }

    public List<PassoProducao> getPassos() { return passos; }
    public void setPassos(List<PassoProducao> passos) { this.passos = passos; }
}
