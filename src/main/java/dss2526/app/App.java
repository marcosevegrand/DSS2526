package dss2526.app;

import dss2526.data.contract.*;
import dss2526.data.impl.*;
import dss2526.domain.entity.Estacao;
import dss2526.service.gestao.*;
import dss2526.service.producao.*;
import dss2526.service.venda.*;
import dss2526.ui.controller.*;
import dss2526.ui.view.AppUI;

public class App {

    public static void main(String[] args) {
        // 1. Instanciar DAOs (Camada de Dados)
        CatalogoDAO catalogoDAO = CatalogoDAOImpl.getInstance();
        EstacaoDAO estacaoDAO = EstacaoDAOImpl.getInstance();
        FuncionarioDAO funcionarioDAO = FuncionarioDAOImpl.getInstance();
        MenuDAO menuDAO = MenuDAOImpl.getInstance();
        ProdutoDAO produtoDAO = ProdutoDAOImpl.getInstance();
        IngredienteDAO ingredienteDAO = IngredienteDAOImpl.getInstance();
        PedidoDAO pedidoDAO = PedidoDAOImpl.getInstance();
        PassoDAO passoDAO = PassoDAOImpl.getInstance();
        TarefaDAO tarefaDAO = TarefaDAOImpl.getInstance();
        RestauranteDAO restauranteDAO = RestauranteDAOImpl.getInstance();
        MensagemDAO mensagemDAO = MensagemDAOImpl.getInstance();


        // 2. Instanciar Facades (Camada de Serviço)
        IVendaFacade vendaFacade = new VendaFacade(pedidoDAO, produtoDAO, menuDAO);
        IProducaoFacade producaoFacade = new ProducaoFacade(tarefaDAO, pedidoDAO, ingredienteDAO);
        IGestaoFacade gestaoFacade = new GestaoFacade(pedidoDAO, produtoDAO);


        // Instanciar Controllers (Camada de Controle)
        VendaController vendaController = new VendaController(vendaFacade);
        ProducaoController producaoController = new ProducaoController(producaoFacade);
        GestaoController gestaoController = new GestaoController(gestaoFacade);

        // 3. Instanciar e iniciar a App UI
        AppUI appUI = new AppUI(vendaController, producaoController, gestaoController);
        appUI.run();
    }
}