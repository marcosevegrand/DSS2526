package restaurante.business.pedidos;

/**
 * Represents an ingredient used in food items
 */
public class Ingrediente {
    private String id;
    private String nome;
    private boolean isAlergenico;
    private Alergenico alergenico;
    private Stock stock;
    
    public Ingrediente(String id, String nome) {
        this.id = id;
        this.nome = nome;
        this.isAlergenico = false;
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
    
    public boolean isAlergenico() {
        return isAlergenico;
    }
    
    public void setAlergenico(boolean alergenico) {
        isAlergenico = alergenico;
    }
    
    public Alergenico getAlergenico() {
        return alergenico;
    }
    
    public void setAlergenico(Alergenico alergenico) {
        this.alergenico = alergenico;
        this.isAlergenico = true;
    }
    
    public Stock getStock() {
        return stock;
    }
    
    public void setStock(Stock stock) {
        this.stock = stock;
    }
}
