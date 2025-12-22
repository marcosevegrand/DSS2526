package restaurante.business.pedidos;

/**
 * Represents a product in stock with its quantity
 */
public class ItemStock {
    private Produto produto;
    private int quantidade;
    
    public ItemStock(Produto produto, int quantidade) {
        this.produto = produto;
        this.quantidade = quantidade;
    }
    
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
    
    public void adicionarQuantidade(int qtd) {
        this.quantidade += qtd;
    }
    
    public void removerQuantidade(int qtd) {
        if (this.quantidade >= qtd) {
            this.quantidade -= qtd;
        }
    }
    
    public boolean temStock() {
        return quantidade > 0;
    }
}
