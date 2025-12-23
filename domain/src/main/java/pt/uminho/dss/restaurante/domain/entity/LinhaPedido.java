package pt.uminho.dss.restaurante.domain.entity;

import java.util.Objects;

/**
 * Linha de um pedido. Suporta produto OU menu.
 * Implementação mínima: armazena referência ao item, quantidade e preço unitário.
 */
public class LinhaPedido {

    private Integer id;
    private Produto produto; // se este campo != null então é um produto
    private Menu menu;       // se este campo != null então é um menu
    private int quantidade;
    private double precoUnitario;

    public LinhaPedido(Produto produto, int quantidade) {
        this.produto = Objects.requireNonNull(produto);
        this.menu = null;
        this.quantidade = Math.max(1, quantidade);
        this.precoUnitario = produto.getPreco();
    }

    public LinhaPedido(Menu menu, int quantidade) {
        this.menu = Objects.requireNonNull(menu);
        this.produto = null;
        this.quantidade = Math.max(1, quantidade);
        this.precoUnitario = menu.getPreco();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Produto getProduto() {
        return produto;
    }

    public Menu getMenu() {
        return menu;
    }

    public boolean isProduto() {
        return produto != null;
    }

    public boolean isMenu() {
        return menu != null;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        if (quantidade < 0) throw new IllegalArgumentException("quantidade negativa");
        this.quantidade = quantidade;
    }

    public double getPrecoUnitario() {
        return precoUnitario;
    }

    public void setPrecoUnitario(double precoUnitario) {
        this.precoUnitario = precoUnitario;
    }

    public double getSubtotal() {
        return precoUnitario * quantidade;
    }

    public void incrementarQuantidade(int q) {
        if (q <= 0) throw new IllegalArgumentException("incremento deve ser > 0");
        this.quantidade += q;
    }

    /**
     * Decrementa quantidade. Retorna true se a linha fica com quantidade > 0,
     * false se a quantidade chega a 0 (linha deve ser removida pelo pedido).
     */
    public boolean decrementarQuantidade(int q) {
        if (q <= 0) throw new IllegalArgumentException("decremento deve ser > 0");
        this.quantidade -= q;
        if (this.quantidade < 0) this.quantidade = 0;
        return this.quantidade > 0;
    }

    /**
     * Id do item associado (produto ou menu).
     */
    public int getItemId() {
        if (isProduto()) return produto.getId();
        if (isMenu()) return menu.getId();
        throw new IllegalStateException("Linha sem item associado");
    }

    public String getDescricao() {
        if (isProduto()) return produto.getNome();
        if (isMenu()) return menu.getNome();
        return "";
    }

    /**
     * Verifica se pode remover a quantidade solicitada.
     */
    public boolean podeRemover(int quantidade) {
        return this.quantidade >= quantidade;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LinhaPedido)) return false;
        LinhaPedido that = (LinhaPedido) o;
        if (isProduto() && that.isProduto()) {
            return Objects.equals(produto.getId(), that.produto.getId());
        }
        if (isMenu() && that.isMenu()) {
            return Objects.equals(menu.getId(), that.menu.getId());
        }
        return false;
    }

    @Override
    public int hashCode() {
        if (isProduto()) return Objects.hash(produto.getId());
        if (isMenu()) return Objects.hash(menu.getId());
        return 0;
    }

    @Override
    public String toString() {
        return "LinhaPedido{" +
                "item=" + (isProduto() ? produto.getNome() : (isMenu() ? menu.getNome() : "n/a")) +
                ", quantidade=" + quantidade +
                ", precoUnitario=" + precoUnitario +
                '}';
    }
}
