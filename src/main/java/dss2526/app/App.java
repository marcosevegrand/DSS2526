package dss2526.app;

import dss2526.data.contract.*;
import dss2526.data.impl.*;
import dss2526.gestao.*;
import dss2526.producao.*;
import dss2526.venda.*;
import dss2526.ui.view.AppUI;

public class App {

    public static void main(String[] args) {
        // 1. Instanciar DAOs
        CatalogoDAO catalogoDAO = new CatalogoDAOImpl(); // Podes manter se for usado noutro lado
        MenuDAO menuDAO = new MenuDAOImpl();
        ProdutoDAO produtoDAO = new ProdutoDAOImpl();
        IngredienteDAO ingredienteDAO = new IngredienteDAOImpl();
        PedidoDAO pedidoDAO = new PedidoDAOImpl();
        TarefaDAO tarefaDAO = new TarefaDAOImpl();

        // 2. Instanciar Facades com Injeção de Dependências
        IVendaFacade vendaFacade = new VendaFacade(pedidoDAO, produtoDAO, menuDAO);
        IProducaoFacade producaoFacade = new ProducaoFacade(tarefaDAO, pedidoDAO);
        IGestaoFacade gestaoFacade = new GestaoFacade(catalogoDAO, menuDAO, produtoDAO, ingredienteDAO);


        // 3. Instanciar e iniciar a App UI
        AppUI appUI = new AppUI(vendaFacade, producaoFacade, gestaoFacade);
        appUI.run();
    }
}