package restaurante.business.pedidos;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an item in an order
 */
public class Item {
    private String id;
    private Alimento alimento;
    private int quantidade;
    private List<String> ingredientesRemovidos;
    private List<String> notas;
    private Nota notaPersonalizacao;
    
    public Item(String id, Alimento alimento, int quantidade) {
        this.id = id;
        this.alimento = alimento;
        this.quantidade = quantidade;
        this.ingredientesRemovidos = new ArrayList<>();
        this.notas = new ArrayList<>();
    }
    
    public String getId() {
        return id;
    }
    
    public Alimento getAlimento() {
        return alimento;
    }
    
    public int getQuantidade() {
        return quantidade;
    }
    
    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }
    
    public List<String> getIngredientesRemovidos() {
        return new ArrayList<>(ingredientesRemovidos);
    }
    
    public void removerIngrediente(String ingrediente) {
        this.ingredientesRemovidos.add(ingrediente);
    }
    
    public List<String> getNotas() {
        return new ArrayList<>(notas);
    }
    
    public void adicionarNota(String nota) {
        this.notas.add(nota);
    }
    
    public Nota getNotaPersonalizacao() {
        return notaPersonalizacao;
    }
    
    public void setNotaPersonalizacao(Nota notaPersonalizacao) {
        this.notaPersonalizacao = notaPersonalizacao;
    }
    
    public double getPreco() {
        return alimento.getPreco().getValor() * quantidade;
    }
}
