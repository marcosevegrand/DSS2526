package dss2526.service.gestao;

import dss2526.service.base.BaseFacade;
import dss2526.data.impl.*;
import dss2526.data.contract.*;
import dss2526.domain.entity.*;
import java.util.List;

public class GestaoFacade extends BaseFacade implements IGestaoFacade {

    private static GestaoFacade instance;

    // References to DAOs

    private final RestauranteDAO restauranteDAO;
    private final EstacaoDAO estacaoDAO;
    private final FuncionarioDAO funcionarioDAO;

    private final CatalogoDAO catalogoDAO;
    private final MenuDAO menuDAO;
    private final ProdutoDAO produtoDAO;

    private final IngredienteDAO ingredienteDAO;

    private GestaoFacade() {
        this.restauranteDAO = RestauranteDAOImpl.getInstance();
        this.estacaoDAO = EstacaoDAOImpl.getInstance();
        this.funcionarioDAO = FuncionarioDAOImpl.getInstance();
        
        this.catalogoDAO = CatalogoDAOImpl.getInstance();
        this.menuDAO = MenuDAOImpl.getInstance();
        this.produtoDAO = ProdutoDAOImpl.getInstance();

        this.ingredienteDAO = IngredienteDAOImpl.getInstance();
    }

    public static synchronized GestaoFacade getInstance() {
        if (instance == null) {
            instance = new GestaoFacade();
        }
        return instance;
    }

    // ...
}