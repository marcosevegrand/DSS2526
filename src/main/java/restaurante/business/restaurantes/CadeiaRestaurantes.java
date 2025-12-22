package restaurante.business.restaurantes;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the restaurant chain
 */
public class CadeiaRestaurantes {
    private String id;
    private String nome;
    private List<Restaurante> restaurantes;
    
    public CadeiaRestaurantes(String id, String nome) {
        this.id = id;
        this.nome = nome;
        this.restaurantes = new ArrayList<>();
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
    
    public List<Restaurante> getRestaurantes() {
        return new ArrayList<>(restaurantes);
    }
    
    public void adicionarRestaurante(Restaurante restaurante) {
        this.restaurantes.add(restaurante);
    }
    
    public void removerRestaurante(Restaurante restaurante) {
        this.restaurantes.remove(restaurante);
    }
}
