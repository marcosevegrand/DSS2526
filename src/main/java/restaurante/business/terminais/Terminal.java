package restaurante.business.terminais;

/**
 * Base class for terminals in the restaurant
 */
public abstract class Terminal {
    private String id;
    private String localizacao;
    private boolean operacional;
    
    public Terminal(String id, String localizacao) {
        this.id = id;
        this.localizacao = localizacao;
        this.operacional = true;
    }
    
    public String getId() {
        return id;
    }
    
    public String getLocalizacao() {
        return localizacao;
    }
    
    public void setLocalizacao(String localizacao) {
        this.localizacao = localizacao;
    }
    
    public boolean isOperacional() {
        return operacional;
    }
    
    public void setOperacional(boolean operacional) {
        this.operacional = operacional;
    }
    
    public abstract String getTipo();
}
