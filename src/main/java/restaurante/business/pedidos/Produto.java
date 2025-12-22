package restaurante.business.pedidos;

/**
 * Represents a product in the catalog
 */
public class Produto {
    private int id;
    private String nome;
    private float preco;
    private String descricao;
    private String tipo;  // ← NOVO CAMPO: "HAMBURGUER", "GELADO", "BEBIDA", etc.
    
    public Produto(int id, String nome, float preco, String tipo) {
        this.id = id;
        this.nome = nome;
        this.preco = preco;
        this.descricao = "";
        this.tipo = tipo;
    }
    
    // Construtor compatível com código existente (tipo por defeito)
    public Produto(int id, String nome, float preco) {
        this(id, nome, preco, "GERAL");  // tipo padrão
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getNome() {
        return nome;
    }
    
    public void setNome(String nome) {
        this.nome = nome;
    }
    
    public float getPreco() {
        return preco;
    }
    
    public void setPreco(float preco) {
        this.preco = preco;
    }
    
    public String getDescricao() {
        return descricao;
    }
    
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    
    public String getTipo() {
        return tipo;
    }
    
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
