package pt.uminho.dss.fastfood.persistence.contract;

import pt.uminho.dss.fastfood.core.domain.entity.Catalogo;

public interface CatalogoDAO extends GenericDAO<Catalogo, Integer> {
    // Aqui normalmente não é preciso muito mais; o conteúdo do catálogo
    // é recuperado via relações para Produto e Menu.
}
