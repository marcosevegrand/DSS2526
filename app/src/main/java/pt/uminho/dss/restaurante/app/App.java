package pt.uminho.dss.restaurante.app;

import pt.uminho.dss.restaurante.core.domain.entity.Produto;
import pt.uminho.dss.restaurante.persistence.impl.*;
import pt.uminho.dss.restaurante.ui.controllers.VendaController;
import pt.uminho.dss.restaurante.ui.views.TerminalVendaView;
import pt.uminho.dss.restaurante.venda.VendaFacade;

/**
 * Classe principal de arranque (Composition Root).
 * Responsável por montar o sistema e injetar as dependências.
 */
public class MainApp {

    public static void main(String[] args) {
        System.out.println("=== FAST FOOD SYSTEM - BOOTING ===");

        // 1. Inicializar Persistência (Model - Infraestrutura)
        var pedidoDAO = new PedidoDAOInMemory();
        var produtoDAO = new ProdutoDAOInMemory();
        // Nota: Menu e Talao podem ser null ou implementações vazias para este teste

        // 2. Popular Catálogo de Teste
        popularProdutosTeste(produtoDAO);

        // 3. Inicializar Lógica de Negócio (Model - Domínio)
        // Injetamos os DAOs na Facade
        VendaFacade vendaFacade = new VendaFacade(
            pedidoDAO,
            produtoDAO,
            null,
            null
        );

        // 4. Inicializar UI Logic (Controller)
        // Injetamos a Facade no Controller
        VendaController vendaController = new VendaController(vendaFacade, 1);

        // 5. Inicializar Interface (View)
        // Injetamos o Controller na View
        TerminalVendaView view = new TerminalVendaView(vendaController);
    }

    private static void popularProdutosTeste(ProdutoDAOInMemory dao) {
        // Criando produtos fictícios para o teste
        // Assumindo construtor: Produto(int id, String nome, double preco)
        dao.save(new Produto(1, "Hamburguer Clássico", 5.50));
        dao.save(new Produto(2, "Batatas Fritas M", 2.00));
        System.out.println("[SYS] Catálogo de teste populado.");
    }
}
