package pt.uminho.dss.restaurante.domain.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Catalogo implements Serializable {
    private Long id;
    private List<Produto> produtos = new ArrayList<>();
    private List<Menu> menus = new ArrayList<>();

    public Catalogo() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public List<Produto> getProdutos() { return produtos; }
    public void setProdutos(List<Produto> produtos) { this.produtos = produtos; }
    public List<Menu> getMenus() { return menus; }
    public void setMenus(List<Menu> menus) { this.menus = menus; }
}