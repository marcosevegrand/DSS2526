package dss2526.app;

import dss2526.data.contract.*;
import dss2526.data.impl.*;
import dss2526.domain.contract.*;
import dss2526.domain.entity.*;
import dss2526.gestao.*;
import dss2526.producao.*;
import dss2526.venda.*;
import dss2526.ui.controller.*;
import dss2526.ui.view.*;

import javax.swing.*;
import java.awt.*;

/**
 * Fachada Mestre (Sistema) que garante a unicidade do Inventário.
 * Inicializa DAOs, cria fachadas, controllers e lança UIs para teste.
 */
public class App {

    private final ProdutoDAO produtoDAO;
    private final MenuDAO menuDAO;
    private final PedidoDAO pedidoDAO;
    private final IngredienteDAO ingredienteDAO;
    private final TarefaDAO tarefaDAO;

    private VendaFacade vendaFacade;
    private VendaController vendaController;

    // Produção (singletons para UI)
    private ProducaoFacade producaoFacade;
    private ProducaoController producaoController;

    // Estatísticas
    private GestaoFacade gestaoFacade;
    private GestaoController gestaoController;

    public App() {
        // Inicializa DAOs
        this.produtoDAO = new ProdutoDAOImpl();
        this.menuDAO = new MenuDAOImpl();
        this.pedidoDAO = new PedidoDAOImpl();
        this.ingredienteDAO = new IngredienteDAOImpl();
        this.tarefaDAO = new TarefaDAOImpl();

        // Facade / controllers
        this.vendaFacade = new VendaFacade(produtoDAO, menuDAO, pedidoDAO);
        this.vendaController = new VendaController(vendaFacade);

        this.producaoFacade = new ProducaoFacade(tarefaDAO);
        this.producaoController = new ProducaoController(producaoFacade);

        this.gestaoFacade = new GestaoFacade(pedidoDAO);
        this.gestaoController = new GestaoController(gestaoFacade);
    }

    public static void main(String[] args) {
        App app = new App();
    }
}