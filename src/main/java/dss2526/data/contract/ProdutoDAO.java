// ...existing code...
package pt.uminho.dss.restaurante.persistence.contract;

import java.util.List;
import java.util.Optional;

import pt.uminho.dss.restaurante.domain.entity.Produto;

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
