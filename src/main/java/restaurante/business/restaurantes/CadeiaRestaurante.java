package restaurante.business.restaurantes;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents the restaurant chain
 */
public class CadeiaRestaurante {
    private int id;
    private String nome;
    private Map<Integer, Restaurante> restaurantes;
    
    public CadeiaRestaurante(int id, String nome) {
        this.id = id;
        this.nome = nome;
        this.restaurantes = new HashMap<>();
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getNome() {
        return nome;
    }
    
    public void setNome(String nome) {
        this.nome = nome;
    }
    
    public Map<Integer, Restaurante> getRestaurantes() {
        return new HashMap<>(restaurantes);
    }
    
    public Restaurante selecionarRestaurante(int id) {
        return restaurantes.get(id);
    }
    
    public void adicionarRestaurante(Restaurante restaurante) {
        restaurantes.put(restaurante.getId(), restaurante);
    }
    
    public void removerRestaurante(int id) {
        restaurantes.remove(id);
    }
}
