package pt.uminho.dss.fastfood.core.domain.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import pt.uminho.dss.fastfood.core.domain.entity.Produto;

public class Stock {

    private int id;
    private int idRestaurante; // FK para o restaurante a que pertence

    private List<ItemStock> itens;

    public Stock(int idRestaurante) {
        this.idRestaurante = idRestaurante;
        this.itens = new ArrayList<>();
    }

    protected Stock() {
        this.itens = new ArrayList<>();
    }

    // ------------------------------
    // Operações de gestão de stock
    // ------------------------------

    public void adicionarProduto(Produto produto, int quantidade) {
        if (produto == null) {
            throw new IllegalArgumentException("Produto não pode ser null.");
        }
        if (quantidade < 0) {
            throw new IllegalArgumentException(
                "Quantidade não pode ser negativa."
            );
        }

        Optional<ItemStock> existente = encontrarItemPorProdutoId(
            produto.getId()
        );
        if (existente.isPresent()) {
            existente.get().aumentar(quantidade);
        } else {
            itens.add(new ItemStock(produto, quantidade));
        }
    }

    public void removerProduto(int idProduto, int quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser positiva.");
        }

        ItemStock item = encontrarItemPorProdutoId(idProduto).orElseThrow(() ->
            new IllegalStateException("Produto não existe no stock.")
        );

        item.diminuir(quantidade);

        if (item.getQuantidade() == 0) {
            itens.remove(item);
        }
    }

    public int consultarQuantidade(int idProduto) {
        return encontrarItemPorProdutoId(idProduto)
            .map(ItemStock::getQuantidade)
            .orElse(0);
    }

    public boolean temDisponivel(int idProduto, int quantidadeNecessaria) {
        return consultarQuantidade(idProduto) >= quantidadeNecessaria;
    }

    private Optional<ItemStock> encontrarItemPorProdutoId(int idProduto) {
        return itens
            .stream()
            .filter(i -> i.getProduto().getId() == idProduto)
            .findFirst();
    }

    // ------------------------------
    // Getters / setters
    // ------------------------------

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdRestaurante() {
        return idRestaurante;
    }

    public void setIdRestaurante(int idRestaurante) {
        this.idRestaurante = idRestaurante;
    }

    public List<ItemStock> getItens() {
        return Collections.unmodifiableList(itens);
    }

    public void setItens(List<ItemStock> itens) {
        this.itens = (itens == null)
            ? new ArrayList<>()
            : new ArrayList<>(itens);
    }
}
