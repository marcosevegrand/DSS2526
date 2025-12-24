package dss2526.app;

import dss2526.data.contract.*;
import dss2526.data.impl.*;
import dss2526.gestao.*;
import dss2526.producao.*;
import dss2526.venda.*;

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
        IGestaoFacade gestaoFacade = new GestaoFacade(catalogoDAO, menuDAO, produtoDAO, ingredienteDAO);
        
        // CORREÇÃO AQUI: Passar pedidoDAO, produtoDAO e menuDAO (3 argumentos)
        IVendaFacade vendaFacade = new VendaFacade(pedidoDAO, produtoDAO, menuDAO); 
        
        IProducaoFacade producaoFacade = new ProducaoFacade(tarefaDAO, pedidoDAO);
    }
}