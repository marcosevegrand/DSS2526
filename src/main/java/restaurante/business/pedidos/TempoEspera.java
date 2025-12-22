package restaurante.business.pedidos;

/**
 * Represents wait time for an order
 */
public class TempoEspera {
    private int minutos;
    
    public TempoEspera(int minutos) {
        this.minutos = minutos;
    }
    
    public int getMinutos() {
        return minutos;
    }
    
    public void setMinutos(int minutos) {
        this.minutos = minutos;
    }
}
