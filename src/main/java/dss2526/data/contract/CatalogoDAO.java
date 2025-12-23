package dss2526.data.contract;

import dss2526.domain.entity.Catalogo;

public interface CatalogoDAO extends GenericDAO<Catalogo, Integer> {
    // Aqui normalmente não é preciso muito mais; o conteúdo do catálogo
    // é recuperado via relações para Produto e Menu.
}
