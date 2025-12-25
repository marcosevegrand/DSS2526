package dss2526.domain.entity;

import java.util.*;

public class Menu {
    private int id;
    private String nome;
    private double preco;
    private List<LinhaMenu> linhasMenu = new ArrayList<>();

    // Construtores

    public Menu() {}

    // Getters e Setters

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public double getPreco() { return preco; }
    public void setPreco(double preco) { this.preco = preco; }

    public List<LinhaMenu> getLinhasMenu() { return linhasMenu; }
    public void setLinhasMenu(List<LinhaMenu> linhasMenu) { this.linhasMenu = linhasMenu; }
}