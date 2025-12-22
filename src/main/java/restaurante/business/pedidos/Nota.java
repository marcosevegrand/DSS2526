package restaurante.business.pedidos;

/**
 * Represents a note or customization instruction for an item
 */
public class Nota {
    private String texto;
    
    public Nota(String texto) {
        this.texto = texto;
    }
    
    public String getTexto() {
        return texto;
    }
    
    public void setTexto(String texto) {
        this.texto = texto;
    }
}
