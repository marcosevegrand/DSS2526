package dss2526.domain.entity;

import dss2526.domain.contract.Item;

import java.util.*;

public class Menu implements Item {
    private int id;
    private String nome;
    private double preco;
    private boolean disponivel;
    private List<LinhaMenu> linhasMenu = new ArrayList<>();

    // Construtores

    public Menu() {}

    public Menu(String nome, double preco, List<LinhaMenu> linhasMenu) {
        this.nome = nome;
        this.preco = preco;
        this.linhasMenu = linhasMenu;
    }

    // Getters e Setters

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public double getPreco() { return preco; }
    public void setPreco(double preco) { this.preco = preco; }

    public boolean isDisponivel() { return disponivel; }
    public void setDisponivel(boolean disponivel) { this.disponivel = disponivel; }

    public List<LinhaMenu> getLinhasMenu() { return linhasMenu; }
    public void setLinhasMenu(List<LinhaMenu> linhasMenu) { this.linhasMenu = linhasMenu; }
}