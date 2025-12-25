package dss2526.app;

import dss2526.data.contract.*;
import dss2526.data.impl.*;
import dss2526.service.gestao.*;
import dss2526.service.producao.*;
import dss2526.service.venda.*;
import dss2526.ui.controller.*;
import dss2526.ui.view.AppUI;

public class App {

    public static void main(String[] args) {
        // 1. Instanciar DAOs (Camada de Dados)
        CatalogoDAO catalogoDAO = new CatalogoDAOImpl();
        MenuDAO menuDAO = new MenuDAOImpl();
        ProdutoDAO produtoDAO = new ProdutoDAOImpl();
        IngredienteDAO ingredienteDAO = new IngredienteDAOImpl();
        PedidoDAO pedidoDAO = new PedidoDAOImpl();
        PassoDAO tarefaDAO = new PassoDAOImpl();

        // 2. Instanciar Facades (Camada de Serviço)
        IProducaoFacade producaoFacade = new ProducaoFacade(tarefaDAO, pedidoDAO, ingredienteDAO);
        IGestaoFacade gestaoFacade = new GestaoFacade(pedidoDAO, produtoDAO, producaoFacade);
        IVendaFacade vendaFacade = new VendaFacade(pedidoDAO, produtoDAO, menuDAO);

        // Instanciar Controllers (Camada de Controle)
        VendaController vendaController = new VendaController(vendaFacade);
        ProducaoController producaoController = new ProducaoController(producaoFacade);
        GestaoController gestaoController = new GestaoController(gestaoFacade);

        // 3. Instanciar e iniciar a App UI
        AppUI appUI = new AppUI(vendaController, producaoController, gestaoController);
        appUI.run();
    }
}