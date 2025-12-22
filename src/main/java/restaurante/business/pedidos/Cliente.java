package restaurante.business.pedidos;

/**
 * Represents a client in the system
 */
public class Cliente {
    private String id;
    private String nome;
    
    public Cliente(String id) {
        this.id = id;
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
}
