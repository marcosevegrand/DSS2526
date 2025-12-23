package pt.uminho.dss.restaurante.domain.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Representa um Menu (conjunto de produtos) com preço e lista de produtos.
 * Implementação mínima compatível com LinhaPedido (getId, getNome, getPreco).
 */
public class Menu {

    private Integer id;
    private String nome;
    private double preco;
    private final List<Produto> produtos = new ArrayList<>();

    public Menu() {
    }

    public Menu(String nome, double preco) {
        this.nome = Objects.requireNonNull(nome);
        this.preco = preco;
    }

    public Menu(Integer id, String nome, double preco) {
        this.id = id;
        this.nome = Objects.requireNonNull(nome);
        this.preco = preco;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = Objects.requireNonNull(nome);
    }

    /**
     * Preço do menu. Pode ser um preço próprio ou calculado externamente.
     */
    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    public List<Produto> getProdutos() {
        return Collections.unmodifiableList(produtos);
    }

    public void addProduto(Produto p) {
        this.produtos.add(Objects.requireNonNull(p));
    }

    public boolean removeProduto(Produto p) {
        return this.produtos.remove(p);
    }

    public boolean isVazio() {
        return produtos.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Menu)) return false;
        Menu menu = (Menu) o;
        if (id != null && menu.id != null) {
            return Objects.equals(id, menu.id);
        }
        return Objects.equals(nome, menu.nome);
    }

    @Override
    public int hashCode() {
        return id != null ? Objects.hash(id) : Objects.hash(nome);
    }

    @Override
    public String toString() {
        return "Menu{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", preco=" + preco +
                ", produtos=" + produtos +
                '}';
    }
}