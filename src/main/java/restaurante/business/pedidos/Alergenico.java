package restaurante.business.pedidos;

/**
 * Represents an allergen
 */
public class Alergenico {
    private String id;
    private String nome;
    private String descricao;
    
    public Alergenico(String id, String nome) {
        this.id = id;
        this.nome = nome;
    }
    
    public String getId() {
        return id;
    }
    
    public String getNome() {
        return nome;
    }
    
    public void setNome(String nome) {
        this.nome = nome;
    }
    
    public String getDescricao() {
        return descricao;
    }
    
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
