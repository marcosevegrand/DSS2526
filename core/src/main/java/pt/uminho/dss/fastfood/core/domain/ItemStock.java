package pt.uminho.dss.fastfood.core.domain;

public class ItemStock {

    private int id;
    private Produto produto;
    private int quantidade;

    public ItemStock(Produto produto, int quantidade) {
        this.produto = produto;
        this.quantidade = quantidade;
    }

    protected ItemStock() { }

    public void aumentar(int delta) {
        if (delta < 0) throw new IllegalArgumentException("Delta não pode ser negativo.");
        quantidade += delta;
    }

    public void diminuir(int delta) {
        if (delta < 0) throw new IllegalArgumentException("Delta não pode ser negativo.");
        if (delta > quantidade) {
            throw new IllegalStateException("Stock insuficiente para " + produto.getNome());
        }
        quantidade -= delta;
    }

    // Getters / setters

    public int getId() {
        return id;
    }

    public void setId(int id) { this.id = id; }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }
}
