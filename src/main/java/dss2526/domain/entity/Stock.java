package dss2526.domain.entity;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public class Stock implements Serializable {
    private Integer id; // não sei se deve exister
    private List<LinhaStock> ingredientes;

    // Construtores

    public Stock() {}

    public Stock(List<LinhaStock> ingredientes) {
        this.ingredientes = ingredientes;
    }

    // Lógica Simples

    /**
     * Adiciona uma quantidade ao stock de um ingrediente específico.
     */ 
    public boolean adicionarQuantidade(int ingredienteId, int quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade a adicionar deve ser positiva.");
        }

        Optional<LinhaStock> linha = ingredientes.stream()
                .filter(l -> l.getIngrediente().getId() == ingredienteId)
                .findFirst();

        if (linha.isPresent()) {
            linha.get().setQuantidade(linha.get().getQuantidade() + quantidade);
            return true;
        }

        return false; // Ingrediente não encontrado
    }

    /**
     * Adiciona uma quantidade de um ingrediente ao stock.
     */
    public boolean removerQuantidade(int ingredienteId, int quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade a remover deve ser positiva.");
        }

        Optional<LinhaStock> linha = ingredientes.stream()
                .filter(l -> l.getIngrediente().getId() == ingredienteId)
                .findFirst();

        if (linha.isPresent()) {
            LinhaStock linhaStock = linha.get();
            if (linhaStock.getQuantidade() < quantidade) {
                throw new IllegalArgumentException("Quantidade insuficiente no stock.");
            }

            linhaStock.setQuantidade(linhaStock.getQuantidade() - quantidade);
            return true;
        }

        return false; // Ingrediente não encontrado
    }

    /**
     * Verifica se um ingrediente está disponível no stock em uma quantidade mínima.
     */
    public boolean verificarDisponibilidade(int ingredienteId, int quantidade) {
        return ingredientes.stream()
                .filter(l -> l.getIngrediente().getId() == ingredienteId)
                .anyMatch(l -> l.getQuantidade() >= quantidade);
    }

    // Getters e Setters

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public List<LinhaStock> getIngredientes() { return ingredientes; }
    public void setIngredientes(List<LinhaStock> ingredientes) { this.ingredientes = ingredientes; }
    
}