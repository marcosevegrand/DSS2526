package restaurante.business.funcionarios;

/**
 * Represents a work station in the restaurant
 */
public class EstacaoTrabalho {
    private String id;
    private String nome;
    private String tipo; // "Producao" or "Venda"
    
    public EstacaoTrabalho(String id, String nome, String tipo) {
        this.id = id;
        this.nome = nome;
        this.tipo = tipo;
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
    
    public String getTipo() {
        return tipo;
    }
    
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
