package pt.uminho.dss.restaurante.persistence.contract;

import java.util.List;
import java.util.Optional;
import pt.uminho.dss.restaurante.core.domain.entity.Produto;

public interface ProdutoDAO extends GenericDAO<Produto, Integer> {
    /**
     * Procura produtos por nome (ou parte do nome).
     */
    List<Produto> findByNome(String nomeParcial);

    /**
     * Procura produtos por categoria (ex.: HAMBURGUER, BEBIDA, SOBREMESA).
     */
    List<Produto> findByCategoria(String categoria);

    /**
     * Devolve o produto com nome exato, se existir.
     */
    Optional<Produto> findByNomeExato(String nome);
}
