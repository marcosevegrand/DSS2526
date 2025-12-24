package dss2526.app;

import dss2526.data.contract.*;
import dss2526.data.impl.*;
import dss2526.gestao.*;
import dss2526.producao.*;
import dss2526.venda.*;

public class App {

    public static void main(String[] args) {
        // 1. Instantiate DAOs (Data Access Layer)
        // These are the single instances that will be reused throughout the app
        CatalogoDAO catalogoDAO = new CatalogoDAOImpl();
        MenuDAO menuDAO = new MenuDAOImpl();
        ProdutoDAO produtoDAO = new ProdutoDAOImpl();
        IngredienteDAO ingredienteDAO = new IngredienteDAOImpl();
        PedidoDAO pedidoDAO = new PedidoDAOImpl();
        TarefaDAO tarefaDAO = new TarefaDAOImpl();
        // Assuming you might have a StockDAO or similar for production, add it here

        // 2. Instantiate Facades (Service Layer) with Dependency Injection
        // We inject the specific DAOs required by each Facade
        IGestaoFacade gestaoFacade = new GestaoFacade(catalogoDAO, menuDAO, produtoDAO, ingredienteDAO);
        IVendaFacade vendaFacade = new VendaFacade(pedidoDAO, catalogoDAO); // Venda needs access to products/catalogs and orders
        IProducaoFacade producaoFacade = new ProducaoFacade(tarefaDAO, pedidoDAO); // Production needs tasks and orders
    }
}