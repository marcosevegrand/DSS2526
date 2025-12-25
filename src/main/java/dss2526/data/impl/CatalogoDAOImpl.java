package dss2526.data.impl;

import dss2526.data.contract.CatalogoDAO;
import dss2526.domain.entity.Catalogo;

import java.util.Collections;
import java.util.List;

public class CatalogoDAOImpl implements CatalogoDAO {

    private static CatalogoDAOImpl instance;
    
    public static CatalogoDAOImpl getInstance() {
        if(instance == null) instance = new CatalogoDAOImpl();
        return instance;
    }
    
    private CatalogoDAOImpl() {}

    @Override
    public Catalogo create(Catalogo obj) {
        return obj; 
    }

    @Override
    public Catalogo findById(Integer id) {
        Catalogo c = new Catalogo();
        // Populate with all data
        c.setMenus(MenuDAOImpl.getInstance().findAll());
        c.setProdutos(ProdutoDAOImpl.getInstance().findAll());
        return c;
    }

    @Override
    public Catalogo update(Catalogo obj) {
        return obj;
    }

    @Override
    public boolean delete(Integer id) {
        return false;
    }

    @Override
    public List<Catalogo> findAll() {
        return Collections.singletonList(findById(null));
    }
}