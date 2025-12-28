package dss2526.domain.entity;

import java.util.*;
import dss2526.domain.contract.Item;

public class Menu implements Item {
    private int id;
    private String nome;
    private double preco;
    private List<LinhaMenu> linhas = new ArrayList<>();

    // Construtores

    public Menu() {}

    // Getters e Setters

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public double getPreco() { return preco; }
    public void setPreco(double preco) { this.preco = preco; }

    public List<LinhaMenu> getLinhas() { return linhas; }
    public void setLinhas(List<LinhaMenu> linhas) { this.linhas = linhas; }
    public void addLinha(LinhaMenu linha) { this.linhas.add(linha); }
    public void removeLinha(LinhaMenu linha) { this.linhas.remove(linha); }
}