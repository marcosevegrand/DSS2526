package restaurante.business.pedidos;

/**
 * Represents preparation time for a food item
 */
public class TempoPreparacao {
    private int minutos;
    
    public TempoPreparacao(int minutos) {
        this.minutos = minutos;
    }
    
    public int getMinutos() {
        return minutos;
    }
    
    public void setMinutos(int minutos) {
        this.minutos = minutos;
    }
}
