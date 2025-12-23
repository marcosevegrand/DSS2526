package pt.uminho.dss.restaurante.persistence.contract;

import pt.uminho.dss.restaurante.core.domain.entity.Catalogo;

public interface CatalogoDAO extends GenericDAO<Catalogo, Integer> {
    // Aqui normalmente não é preciso muito mais; o conteúdo do catálogo
    // é recuperado via relações para Produto e Menu.
}
