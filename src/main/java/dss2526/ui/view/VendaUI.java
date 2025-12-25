package dss2526.ui.view;

import java.util.*;

import dss2526.ui.controller.VendaController;
import dss2526.ui.delegate.NewMenu;

import dss2526.domain.entity.Pedido;

public class VendaUI {

    private final VendaController venda;
    private final Scanner sc;

    // Estado da venda atual
    private Pedido pedidoAtual;

    public VendaUI(VendaController venda) {
        this.venda = venda;
        this.sc = new Scanner(System.in);
    }

    /**
     * Menu Principal de Vendas
     */
    public void show() {
        NewMenu menu = new NewMenu(
            "Subsistema de Venda",
            new String[] {
            "Iniciar um novo pedido"
        });
        
        menu.setHandler(1, () -> iniciarVenda());
        
        menu.run();
    }

    /**
     * Inicia o ciclo de vida de um pedido
     */
    private void iniciarVenda() {
        // Passo 1: Configuração Inicial
        System.out.println("\n>> Nova Venda");
        String opcaoLevar = lerString("É para levar? (S/N): ");
        boolean paraLevar = opcaoLevar.equalsIgnoreCase("S");

        try {
            // Cria o pedido no sistema via Facade
            // this.pedidoAtual = venda.criarPedido(paraLevar);
            System.out.println(">> Pedido #" + pedidoAtual.getId() + " iniciado.");
            
            // Entra no loop de gestão do pedido
            menuPedidoEmCurso();
            
        } catch (Exception e) {
            System.out.println("Erro ao iniciar pedido: " + e.getMessage());
        }
    }

    private void menuPedidoEmCurso() {
        // ... um pedido pode lhe ser adicionado/removido items (através de linhaPedido com quantidade e uma nota)
        // cancelar ou finalizar o pedido
    }

    private Integer lerInt(String msg) {
        System.out.print(msg);
        return Integer.parseInt(sc.nextLine());
    }

    private String lerString(String msg) {
        System.out.print(msg);
        return sc.nextLine();
    }
}