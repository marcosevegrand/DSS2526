package dss2526.domain.entity;

public class LinhaProduto {
    private int id;
    private int produtoId;
    private int ingredienteId;
    private int quantidade;

    // Construtores

    public LinhaProduto() {}

    // Getters e Setters

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getProdutoId() { return produtoId; }
    public void setProdutoId(int produtoId) { this.produtoId = produtoId; }

    public int getIngredienteId() { return ingredienteId; }
    public void setIngredienteId(int ingredienteId) { this.ingredienteId = ingredienteId; }
    
    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }

    @Override
    public String toString() {
        return "LinhaProduto{" +
                "id=" + id +
                ", produtoId=" + produtoId +
                ", ingredienteId=" + ingredienteId +
                ", quantidade=" + quantidade +
                '}';
    }
}