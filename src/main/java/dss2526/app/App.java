package dss2526.app;

import dss2526.data.contract.*;
import dss2526.data.impl.*;
import dss2526.gestao.*;
import dss2526.producao.*;
import dss2526.venda.*;
import dss2526.ui.view.AppUI;

public class App {

    public static void main(String[] args) {
        // 1. Instanciar DAOs (Camada de Dados)
        // Adicionados os DAOs que faltavam para satisfazer os novos construtores
        CatalogoDAO catalogoDAO = new CatalogoDAOImpl();
        MenuDAO menuDAO = new MenuDAOImpl();
        ProdutoDAO produtoDAO = new ProdutoDAOImpl();
        IngredienteDAO ingredienteDAO = new IngredienteDAOImpl();
        PedidoDAO pedidoDAO = new PedidoDAOImpl();
        TarefaDAO tarefaDAO = new TarefaDAOImpl();
        
        // Novos DAOs necessários para Gestão e Produção
        RestauranteDAO restauranteDAO = new RestauranteDAOImpl();
        FuncionarioDAO funcionarioDAO = new FuncionarioDAOImpl();
        EstacaoDAO estacaoDAO = new EstacaoDAOImpl();

        // 2. Instanciar a Produção
        // Adicionado estacaoDAO (requerido pela ProducaoFacade)
        IProducaoFacade producaoFacade = new ProducaoFacade(tarefaDAO, pedidoDAO, ingredienteDAO, estacaoDAO);

        // 3. Instanciar a Gestão
        // Agora com os 6 argumentos requeridos: Pedido, Produto, Restaurante, Funcionario, Estacao, Producao
        IGestaoFacade gestaoFacade = new GestaoFacade(
            pedidoDAO, 
            produtoDAO, 
            restauranteDAO, 
            funcionarioDAO, 
            estacaoDAO, 
            producaoFacade
        );
        
        // 4. Instanciar a Venda
        // Adicionada a producaoFacade (requerida para registar novos pedidos na cozinha)
        IVendaFacade vendaFacade = new VendaFacade(pedidoDAO, produtoDAO, menuDAO, producaoFacade);

        // 5. Iniciar a Interface Gráfica
        AppUI appUI = new AppUI(vendaFacade, producaoFacade, gestaoFacade);
        appUI.run();
    }
}