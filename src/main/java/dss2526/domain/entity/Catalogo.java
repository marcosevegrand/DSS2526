package dss2526.domain.entity;

import java.util.*;

public class Catalogo {
    private int id;
    private String nome;
    private List<Integer> menuIds = new ArrayList<>();
    private List<Integer> produtoIds = new ArrayList<>();

    // Construtores

    public Catalogo() {}

    // Getters e Setters

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    
    public List<Integer> getMenuIds() { return menuIds; }
    public void setMenuIds(List<Integer> menuIds) { this.menuIds = menuIds; }
    public void addMenuId(Integer menuId) { this.menuIds.add(menuId); }
    public void removeMenuId(Integer menuId) { this.menuIds.remove(menuId); }

    public List<Integer> getProdutoIds() { return produtoIds; }
    public void setProdutoIds(List<Integer> produtoIds) { this.produtoIds = produtoIds; }
    public void addProdutoId(Integer produtoId) { this.produtoIds.add(produtoId); }
    public void removeProdutoId(Integer produtoId) { this.produtoIds.remove(produtoId); }

    @Override
    public String toString() {
        return "Catalogo{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", menuIds=" + menuIds +
                ", produtoIds=" + produtoIds +
                '}';
    }
}