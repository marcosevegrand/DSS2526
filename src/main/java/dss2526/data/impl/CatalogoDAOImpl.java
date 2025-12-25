package dss2526.data.impl;

import dss2526.data.contract.CatalogoDAO;
import dss2526.domain.entity.*;
import dss2526.domain.contract.Item;

import java.util.*;

public class CatalogoDAOImpl implements CatalogoDAO {

    @Override
    public Catalogo findById(Integer id) {
        Catalogo c = new Catalogo();
        c.setId(id);
        List<Item> items = new ArrayList<>();
        
        // O catálogo agrega todos os itens de venda disponíveis no sistema
        items.addAll(new ProdutoDAOImpl().findAll());
        items.addAll(new MenuDAOImpl().findAll());
        
        c.setItems(items);
        return c;
    }

    @Override public Catalogo save(Catalogo c) { return c; }
    @Override public Catalogo update(Catalogo c) { return c; }
    @Override public boolean delete(Integer id) { return false; }
    @Override public List<Catalogo> findAll() { return new ArrayList<>(); }
}