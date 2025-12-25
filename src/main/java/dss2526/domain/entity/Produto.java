package dss2526.domain.entity;

import java.util.*;

public class Produto {
    private int id;
    private String nome;
    private double preco;
    private List<LinhaProduto> ingredientes = new ArrayList<>();
    private List<Passo> tarefas = new ArrayList<>();

    // Construtores

    public Produto() {}

    // Getters e Setters

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public double getPreco() { return preco; }
    public void setPreco(double preco) { this.preco = preco; }

    public List<LinhaProduto> getIngredientes() { return ingredientes; }
    public void setIngredientes(List<LinhaProduto> ingredientes) { this.ingredientes = ingredientes; }

    public List<Passo> getTarefas() { return tarefas; }
    public void setTarefas(List<Passo> tarefas) { this.tarefas = tarefas; }
}
