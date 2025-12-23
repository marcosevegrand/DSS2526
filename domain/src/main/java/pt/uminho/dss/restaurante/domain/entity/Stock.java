package pt.uminho.dss.restaurante.domain.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Stock {

    private int idRestaurante;
    // chave = id do Ingrediente, valor = quantidade disponível
    private Map<Integer, Integer> quantidadesPorIngrediente;

    public Stock(int idRestaurante) {
        this.idRestaurante = idRestaurante;
        this.quantidadesPorIngrediente = new HashMap<>();
    }

    public void adicionar(int idIngrediente, int quantidade) {
        if (quantidade <= 0) throw new IllegalArgumentException(
            "Quantidade deve ser positiva."
        );
        quantidadesPorIngrediente.merge(idIngrediente, quantidade, Integer::sum);
    }

    public void removerLista(List<Integer> ingredientesRemover) {
        for (int idIngrediente : ingredientesRemover) {
            Integer quantidade = quantidadesPorIngrediente.get(idIngrediente);
            if (quantidade != null && quantidade > 0) {
                remover(idIngrediente, quantidade);
            }
        }
    }

    public void remover(int idIngrediente, int quantidade) {
        if (quantidade <= 0) throw new IllegalArgumentException(
            "Quantidade deve ser positiva."
        );
        int atual = consultarQuantidade(idIngrediente);
        if (quantidade > atual) throw new IllegalStateException(
            "Stock insuficiente para o ingrediente " + idIngrediente
        );
        int novo = atual - quantidade;
        
        quantidadesPorIngrediente.put(idIngrediente, novo);
    }

    public int consultarQuantidade(int idIngrediente) {
        return quantidadesPorIngrediente.getOrDefault(idIngrediente, 0);
    }

    public boolean temDisponivel(int idIngrediente, int quantidadeNecessaria) {
        return consultarQuantidade(idIngrediente) >= quantidadeNecessaria;
    }

    public int getIdRestaurante() {
        return idRestaurante;
    }

    public String toString() {
        return "Stock do Restaurante " +
            idRestaurante +
            ": " +
            quantidadesPorIngrediente.toString();
    }
}
