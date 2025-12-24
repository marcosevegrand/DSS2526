package dss2526.gestao;

import dss2526.data.contract.CatalogoDAO;
import dss2526.data.contract.IngredienteDAO;
import dss2526.data.contract.MenuDAO;
import dss2526.data.contract.ProdutoDAO;


public class GestaoFacade implements IGestaoFacade {

    private final CatalogoDAO catalogoDAO;
    private final MenuDAO menuDAO;
    private final ProdutoDAO produtoDAO;
    private final IngredienteDAO ingredienteDAO;

    // Constructor Injection: We ask for the DAOs we need
    public GestaoFacade(CatalogoDAO catalogoDAO, MenuDAO menuDAO, ProdutoDAO produtoDAO, IngredienteDAO ingredienteDAO) {
        this.catalogoDAO = catalogoDAO;
        this.menuDAO = menuDAO;
        this.produtoDAO = produtoDAO;
        this.ingredienteDAO = ingredienteDAO;
    }


}
