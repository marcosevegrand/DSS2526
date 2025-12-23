package pt.uminho.dss.fastfood.core.domain.entity;

import java.util.HashMap;
import java.util.Map;

public class Stock {

    private int idRestaurante;
    // chave = id do Produto, valor = quantidade disponível
    private Map<Integer, Integer> quantidadesPorProduto;

    public Stock(int idRestaurante) {
        this.idRestaurante = idRestaurante;
        this.quantidadesPorProduto = new HashMap<>();
    }

    public void adicionar(int idProduto, int quantidade) {
        if (quantidade < 0)
            throw new IllegalArgumentException("Quantidade não pode ser negativa.");
        quantidadesPorProduto.merge(idProduto, quantidade, Integer::sum);
    }

    public void remover(int idProduto, int quantidade) {
        if (quantidade <= 0)
            throw new IllegalArgumentException("Quantidade deve ser positiva.");
        int atual = consultarQuantidade(idProduto);
        if (quantidade > atual)
            throw new IllegalStateException("Stock insuficiente para o produto " + idProduto);
        int novo = atual - quantidade;
        if (novo == 0) {
            quantidadesPorProduto.remove(idProduto);
        } else {
            quantidadesPorProduto.put(idProduto, novo);
        }
    }

    public int consultarQuantidade(int idProduto) {
        return quantidadesPorProduto.getOrDefault(idProduto, 0);
    }

    public boolean temDisponivel(int idProduto, int quantidadeNecessaria) {
        return consultarQuantidade(idProduto) >= quantidadeNecessaria;
    }

    public int getIdRestaurante() {
        return idRestaurante;
    }
}
