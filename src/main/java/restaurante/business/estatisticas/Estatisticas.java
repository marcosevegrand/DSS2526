package restaurante.business.estatisticas;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents statistics data
 */
public class Estatisticas {
    private String tipo;
    private Map<String, Object> dados;
    
    public Estatisticas(String tipo) {
        this.tipo = tipo;
        this.dados = new HashMap<>();
    }
    
    public String getTipo() {
        return tipo;
    }
    
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    
    public Map<String, Object> getDados() {
        return new HashMap<>(dados);
    }
    
    public void adicionarDado(String chave, Object valor) {
        this.dados.put(chave, valor);
    }
    
    public Object obterDado(String chave) {
        return this.dados.get(chave);
    }
}
