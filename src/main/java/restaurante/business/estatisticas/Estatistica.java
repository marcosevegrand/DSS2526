package restaurante.business.estatisticas;

import java.util.Date;

/**
 * Represents statistics data
 */
public class Estatistica {
    private Date inicio;
    private Date fim;
    
    public Estatistica() {
        this.inicio = new Date();
        this.fim = new Date();
    }
    
    public Date getInicio() {
        return inicio;
    }
    
    public void setInicio(Date inicio) {
        this.inicio = inicio;
    }
    
    public Date getFim() {
        return fim;
    }
    
    public void setFim(Date fim) {
        this.fim = fim;
    }
    
    public float calcularFaturacao(Date inicio, Date fim, Date data) {
        // Calculate revenue between dates
        // TODO: Implement business logic
        return 0.0f;
    }
    
    public int tempoMedioAtendimento(Date inicio, Date fim, Date data) {
        // Calculate average service time
        // TODO: Implement business logic
        return 0;
    }
}
