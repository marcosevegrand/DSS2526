package restaurante.business.pedidos;

/**
 * Represents an item in an order (replaces Item class)
 */
public class ItemPedido {
    private int id;
    private int quantidade;
    private String personalizacao;
    private float preco;
    private String nota;
    private int tempoPreparacao; // in minutes
    private Produto produto;
    
    public ItemPedido(int id, Produto produto, int quantidade) {
        this.id = id;
        this.produto = produto;
        this.quantidade = quantidade;
        this.preco = produto.getPreco() * quantidade;
        this.personalizacao = "";
        this.nota = "";
        this.tempoPreparacao = 0;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getQuantidade() {
        return quantidade;
    }
    
    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
        this.preco = produto.getPreco() * quantidade;
    }
    
    public String getPersonalizacao() {
        return personalizacao;
    }
    
    public void setPersonalizacao(String personalizacao) {
        this.personalizacao = personalizacao;
    }
    
    public float getPreco() {
        return preco;
    }
    
    public void setPreco(float preco) {
        this.preco = preco;
    }
    
    public String getNota() {
        return nota;
    }
    
    public void setNota(String nota) {
        this.nota = nota;
    }
    
    public int getTempoPreparacao() {
        return tempoPreparacao;
    }
    
    public void setTempoPreparacao(int tempoPreparacao) {
        this.tempoPreparacao = tempoPreparacao;
    }
    
    public Produto getProduto() {
        return produto;
    }
    
    public void setProduto(Produto produto) {
        this.produto = produto;
    }
}
