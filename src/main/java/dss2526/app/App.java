package dss2526.app;

import dss2526.data.contract.*;
import dss2526.data.impl.*;
import dss2526.gestao.*;
import dss2526.producao.*;
import dss2526.venda.*;

public class App {

    public static void main(String[] args) {
        // 1. Instanciar DAOs (Camada de Dados)
        CatalogoDAO catalogoDAO = new CatalogoDAOImpl();
        MenuDAO menuDAO = new MenuDAOImpl();
        ProdutoDAO produtoDAO = new ProdutoDAOImpl();
        IngredienteDAO ingredienteDAO = new IngredienteDAOImpl();
        PedidoDAO pedidoDAO = new PedidoDAOImpl();
        TarefaDAO tarefaDAO = new TarefaDAOImpl();

        // 2. Instanciar a Produção primeiro
        // Ela precisa do ingredienteDAO para gerir os alertas de falta de stock
        IProducaoFacade producaoFacade = new ProducaoFacade(tarefaDAO, pedidoDAO, ingredienteDAO);

        // 3. Instanciar a Gestão
        // Agora passamos a producaoFacade para que o Gerente possa enviar avisos à cozinha
        IGestaoFacade gestaoFacade = new GestaoFacade(pedidoDAO, produtoDAO, producaoFacade);
        
        // 4. Instanciar a Venda
        IVendaFacade vendaFacade = new VendaFacade(pedidoDAO, produtoDAO, menuDAO);

        // Pronto para iniciar as UIs
        System.out.println("Sistema DSS2526 Inicializado com Sucesso!");
    }
}