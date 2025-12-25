package dss2526.service.gestao;

import dss2526.data.impl.*;
import dss2526.data.contract.*;
import dss2526.domain.entity.*;
import java.util.List;

public class GestaoFacade implements IGestaoFacade {

    private static GestaoFacade instance;

    // References to DAOs
    private final FuncionarioDAO funcionarioDAO;
    private final RestauranteDAO restauranteDAO;
    private final MenuDAO menuDAO;
    private final ProdutoDAO produtoDAO;
    private final IngredienteDAO ingredienteDAO;

    private GestaoFacade() {
        // Initialize DAOs using their Singleton instances
        this.funcionarioDAO = FuncionarioDAOImpl.getInstance();
        this.restauranteDAO = RestauranteDAOImpl.getInstance();
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

    // --- Funcionario Logic ---
    @Override
    public Funcionario registarFuncionario(Funcionario func) {
        return funcionarioDAO.create(func);
    }

    @Override
    public List<Funcionario> listarFuncionarios() {
        return funcionarioDAO.findAll();
    }

    // --- Restaurante Logic ---
    @Override
    public Restaurante criarRestaurante(Restaurante rest) {
        return restauranteDAO.create(rest);
    }

    // --- Menu/Produto Logic ---
    @Override
    public Menu criarMenu(Menu menu) {
        return menuDAO.create(menu);
    }

    @Override
    public Produto criarProduto(Produto produto) {
        return produtoDAO.create(produto);
    }
    
    @Override
    public List<Menu> getMenus() {
        return menuDAO.findAll();
    }

    @Override
    public Ingrediente criarIngrediente(Ingrediente ing) {
        return ingredienteDAO.create(ing);
    }
}