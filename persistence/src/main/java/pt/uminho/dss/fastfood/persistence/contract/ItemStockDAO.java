package pt.uminho.dss.fastfood.persistence.contract;

import java.util.List;
import pt.uminho.dss.fastfood.core.domain.entity.ItemStock;

public interface ItemStockDAO extends GenericDAO<ItemStock, Integer> {
    /**
     * Devolve todos os itens de stock de um restaurante.
     */
    List<ItemStock> findByRestaurante(int idRestaurante);

    /**
     * Procura o item de stock de um certo produto num restaurante.
     * Ideal para verificar/atualizar quantidades.
     */
    ItemStock findByRestauranteAndProduto(int idRestaurante, int idProduto);
}
