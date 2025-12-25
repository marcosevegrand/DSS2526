package dss2526.data.contract;

import dss2526.domain.entity.Menu;
import java.util.Collection;
import java.util.List;

public interface MenuDAO extends GenericDAO<Menu, Integer> {
    
    // Método para obter todos os menus (necessário para a listagem na UI)
    List<Menu> findAll();

    // Métodos de compatibilidade (default) para evitar refatoração na Fachada
    default Menu get(int id) {
        return findById(id);
    }
    
    default Collection<Menu> values() {
        return findAll();
    }

    default void put(int id, Menu m) {
        if (id > 0 && findById(id) != null) {
            update(m);
        } else {
            save(m);
        }
    }
}