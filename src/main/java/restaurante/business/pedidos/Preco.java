package restaurante.business.pedidos;

/**
 * Represents a price value
 */
public class Preco {
    private double valor;
    
    public Preco(double valor) {
        this.valor = valor;
    }
    
    public double getValor() {
        return valor;
    }
    
    public void setValor(double valor) {
        this.valor = valor;
    }
}
