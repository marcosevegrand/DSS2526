package restaurante.business.funcionarios;

/**
 * Represents a work station type (Production or Sales)
 */
public class EstacaoT {
    private int id;
    private String tipo; // "TerminalProducao" or "TerminalVenda"
    private int terminal;
    
    public EstacaoT(int id, String tipo) {
        this.id = id;
        this.tipo = tipo;
        this.terminal = -1;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getTipo() {
        return tipo;
    }
    
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    
    public int getTerminal() {
        return terminal;
    }
    
    public void setTerminal(int terminal) {
        this.terminal = terminal;
    }
}
