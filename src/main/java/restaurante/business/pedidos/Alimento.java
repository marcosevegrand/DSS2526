package restaurante.business.pedidos;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a food item in the menu
 */
public class Alimento {
    private String id;
    private String nome;
    private String descricao;
    private Preco preco;
    private List<Ingrediente> ingredientes;
    private TempoPreparacao tempoPreparacao;
    
    public Alimento(String id, String nome, Preco preco) {
        this.id = id;
        this.nome = nome;
        this.preco = preco;
        this.ingredientes = new ArrayList<>();
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
    
    public String getDescricao() {
        return descricao;
    }
    
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    
    public Preco getPreco() {
        return preco;
    }
    
    public void setPreco(Preco preco) {
        this.preco = preco;
    }
    
    public List<Ingrediente> getIngredientes() {
        return new ArrayList<>(ingredientes);
    }
    
    public void adicionarIngrediente(Ingrediente ingrediente) {
        this.ingredientes.add(ingrediente);
    }
    
    public void removerIngrediente(Ingrediente ingrediente) {
        this.ingredientes.remove(ingrediente);
    }
    
    public TempoPreparacao getTempoPreparacao() {
        return tempoPreparacao;
    }
    
    public void setTempoPreparacao(TempoPreparacao tempoPreparacao) {
        this.tempoPreparacao = tempoPreparacao;
    }
    
    public boolean contemAlergenico(Alergenico alergenico) {
        return ingredientes.stream()
                .anyMatch(ing -> ing.isAlergenico() && ing.getAlergenico().equals(alergenico));
    }
}
