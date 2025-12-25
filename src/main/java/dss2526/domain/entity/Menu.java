package dss2526.domain.entity;

import dss2526.domain.contract.Item;
import java.util.*;

public class Menu implements Item {
    private int id;
    private String nome;
    private double preco;
    private boolean disponivel = true; // Campo necessário para o estado do item
    private List<LinhaMenu> linhasMenu = new ArrayList<>();

    // Construtores
    public Menu() {}

    public Menu(String nome, double preco, List<LinhaMenu> linhasMenu) {
        this.nome = nome;
        this.preco = preco;
        this.linhasMenu = linhasMenu;
        this.disponivel = true;
    }
    
    // Getters e Setters

    @Override
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    @Override
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    @Override
    public double getPreco() { return preco; }
    public void setPreco(double preco) { this.preco = preco; }

    /**
     * Implementação obrigatória da interface Item.
     * Resolve o erro: "must override or implement a supertype method"
     */
    @Override
    public boolean isDisponivel() { 
        return disponivel; 
    }

    public void setDisponivel(boolean disponivel) { 
        this.disponivel = disponivel; 
    }

    public List<LinhaMenu> getLinhasMenu() { return linhasMenu; }
    public void setLinhasMenu(List<LinhaMenu> linhasMenu) { this.linhasMenu = linhasMenu; }
}