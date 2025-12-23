// ...existing code...
package pt.uminho.dss.restaurante.persistence.contract;

import java.util.List;
import java.util.Optional;

import pt.uminho.dss.restaurante.domain.entity.Menu;

/**
 * DAO para Menu — contrato mínimo usado pela fachada/ UI.
 */
public interface MenuDAO {

    Optional<Menu> findById(int id);

    List<Menu> findAll();

    Menu save(Menu menu);

    Menu update(Menu menu);

    void delete(int id);
}
