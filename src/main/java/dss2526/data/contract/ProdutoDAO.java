package dss2526.data.contract;

import java.util.List;
import java.util.Optional;

import dss2526.domain.entity.Produto;

/**
 * DAO para Produto — contrato mínimo usado pela fachada/ UI.
 */
public interface ProdutoDAO {

    Optional<Produto> findById(int id);

    List<Produto> findAll();

    Produto save(Produto produto);

    Produto update(Produto produto);

    void delete(int id);
}
