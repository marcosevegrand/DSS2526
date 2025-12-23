package pt.uminho.dss.fastfood.persistence.contract;

import pt.uminho.dss.fastfood.core.domain.entity.Menu;

import java.util.List;
import java.util.Optional;

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
