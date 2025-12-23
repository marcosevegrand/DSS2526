package pt.uminho.dss.restaurante.persistence.contract;

import java.util.List;
import java.util.Optional;
import pt.uminho.dss.restaurante.core.domain.entity.Menu;

public interface MenuDAO extends GenericDAO<Menu, Integer> {
    /**
     * Procura menus por nome (ou parte do nome).
     */
    List<Menu> findByNome(String nomeParcial);

    /**
     * Devolve o menu com nome exato, se existir.
     */
    Optional<Menu> findByNomeExato(String nome);
}
