package pt.uminho.dss.restaurante.domain.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import pt.uminho.dss.restaurante.domain.contract.Item;

public class Catalogo implements Serializable {
    private Integer id;
    private List<Item> items = new ArrayList<>();
    private List<Ingrediente> ingredientes = new ArrayList<>();
    private List<Alergenico> alergenicos = new ArrayList<>();

    // Construtores

    public Catalogo() {}

    public Catalogo(List<Item> items, List<Ingrediente> ingredientes, List<Alergenico> alergenicos) {
        this.items = items;
        this.ingredientes = ingredientes;
        this.alergenicos = alergenicos;
    }

    // Lógica simples

    public List<Item> getItemsAtivos() {
        return this.items.stream().filter(p -> p.isDisponivel()).collect(Collectors.toList());
    }

    public void adicionarItem(Item item) {
        this.items.add(item);
    }
    public void removerItem(Item item) {
        this.items.remove(item);
    }

    public void adicionarIngrediente(Ingrediente ingrediente) {
        this.ingredientes.add(ingrediente);
    }
    public void removerIngrediente(Ingrediente ingrediente) {
        this.ingredientes.remove(ingrediente);
    }

    public void adicionarAlergenico(Alergenico alergenico) {
        this.alergenicos.add(alergenico);
    }
    public void removerAlergenico(Alergenico alergenico) {
        this.alergenicos.remove(alergenico);
    }

    // Getters e Setters

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public List<Item> getItems() { return items; }
    public void setItems(List<Item> items) { this.items = items; }

    public List<Ingrediente> getIngredientes() { return ingredientes; }
    public void setIngredientes(List<Ingrediente> ingredientes) { this.ingredientes = ingredientes; }

    public List<Alergenico> getAlergenicos() { return alergenicos; }
    public void setAlergenicos(List<Alergenico> alergenicos) { this.alergenicos = alergenicos; }

}