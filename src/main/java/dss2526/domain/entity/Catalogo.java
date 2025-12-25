package dss2526.domain.entity;

import java.util.*;

public class Catalogo {
    private int id;
    private List<Menu> menus = new ArrayList<>();
    private List<Produto> produtos = new ArrayList<>();

    // Construtores

    public Catalogo() {}

    // Lógica simples

    public void adicionarMenu(Menu menu) {
        this.menus.add(menu);
    }
    public void removerMenu(Menu menu) {
        this.menus.remove(menu);
    }

    public void adicionarProduto(Produto produto) {
        this.produtos.add(produto);
    }
    public void removerProduto(Produto produto) {
        this.produtos.remove(produto);
    }

    // Getters e Setters

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public List<Menu> getMenus() { return menus; }
    public void setMenus(List<Menu> menus) { this.menus = menus; }

    public List<Produto> getProdutos() { return produtos; }
    public void setProdutos(List<Produto> produtos) { this.produtos = produtos; }
}