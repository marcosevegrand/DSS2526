package dss2526.domain.entity;

import dss2526.domain.contract.Item;

import java.util.*;

public class Catalogo {
    private int id;
    private List<Item> items = new ArrayList<>();

    // Construtores

    public Catalogo() {}

    public Catalogo(List<Item> items) {
        this.items = items;
    }

    // Lógica simples

    public void adicionarItem(Item item) {
        this.items.add(item);
    }
    public void removerItem(Item item) {
        this.items.remove(item);
    }

    // Getters e Setters

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public List<Item> getItems() { return items; }
    public void setItems(List<Item> items) { this.items = items; }
}