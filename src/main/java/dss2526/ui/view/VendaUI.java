package dss2526.ui.view;

import dss2526.ui.controller.VendaController;
import dss2526.ui.util.NewMenu;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class VendaUI {
    private final VendaController controller;
    private final Scanner sc;

    public VendaUI() {
        this.controller = new VendaController();
        this.sc = new Scanner(System.in);
    }

    public void run() {
        System.out.println("\n===== SUBSISTEMA DE VENDA =====");
        List<String> rests = controller.getNomesRestaurantes();
        if (rests.isEmpty()) return;
        controller.selecionarRestaurante(escolher("Selecione o Restaurante", rests));

        NewMenu menu = new NewMenu("LOJA PRINCIPAL", new String[]{
            "Novo Pedido",
            "Consultar Estado de Pedidos"
        });

        menu.setHandler(1, () -> { fluxoPedido(); return false; });
        menu.setHandler(2, () -> { controller.getAcompanhamento().forEach(System.out::println); return false; });
        menu.run();
    }

    private void fluxoPedido() {
        // 1. Filtros Iniciais
        System.out.println("\n--- RESTRIÇÕES ALIMENTARES ---");
        List<String> alerg = controller.getNomesAlergenicos();
        for(int i=0; i<alerg.size(); i++) System.out.println((i+1) + ". " + alerg.get(i));
        System.out.print("Introduza números dos alergénicos a evitar (ex: 1 2) ou Enter: ");
        String line = sc.nextLine();
        if (!line.isBlank()) {
            List<Integer> sel = new ArrayList<>();
            for(String s : line.split("\\s+")) {
                try { sel.add(Integer.parseInt(s)-1); } catch(Exception e){}
            }
            controller.setAlergenicosPorIndices(sel);
        }

        controller.iniciarPedido();

        // 2. Loop de Gestão de Carrinho
        NewMenu carrinhoMenu = new NewMenu("GESTOR DE PEDIDO", new String[]{
            "Adicionar Item",
            "Remover Item",
            "Finalizar Pedido (Ver Resumo)"
        });

        carrinhoMenu.setHandler(1, () -> {
            List<String> catalogo = controller.getCatalogoFormatado();
            if (catalogo.isEmpty()) { System.out.println("Sem itens disponíveis."); return false; }
            int itemOp = escolher("Catálogo", catalogo);
            System.out.print("Quantidade: "); int qtd = Integer.parseInt(sc.nextLine());
            System.out.print("Observações: "); String obs = sc.nextLine();
            controller.adicionarItem(itemOp, qtd, obs);
            System.out.println("Item adicionado.");
            return false;
        });

        carrinhoMenu.setHandler(2, () -> {
            List<String> itens = controller.getItensNoPedido();
            if (itens.isEmpty()) { System.out.println("O carrinho está vazio."); return false; }
            int remOp = escolher("Remover qual item?", itens);
            controller.removerItem(remOp);
            System.out.println("Item removido.");
            return false;
        });

        carrinhoMenu.setHandler(3, () -> {
            System.out.println(controller.getResumoPedido());
            System.out.print("Confirmar e avançar para pagamento? (s/n): ");
            if (sc.nextLine().equalsIgnoreCase("s")) {
                fluxoPagamento();
                return true; // Sai do menu do carrinho
            }
            return false; // Volta ao menu do carrinho para editar
        });

        // O método run() do NewMenu sai com 0. 
        // Vamos garantir que se o utilizador sair do ciclo (opção 0), o pedido é cancelado.
        System.out.println("\n(Dica: Selecione 0 para Sair/Cancelar o pedido)");
        carrinhoMenu.run();
        
        // Se chegarmos aqui e o pedido ainda estiver "INICIADO", cancelamos.
        controller.cancelarPedidoAtual();
    }

    private void fluxoPagamento() {
        List<String> pags = controller.getOpcoesPagamento();
        int pOp = escolher("Forma de Pagamento", pags);
        System.out.println("\n" + controller.finalizar(pOp));
    }

    private int escolher(String t, List<String> ops) {
        System.out.println("\n--- " + t + " ---");
        for (int i = 0; i < ops.size(); i++) System.out.println((i + 1) + ". " + ops.get(i));
        System.out.print("Seleção: ");
        try { return Integer.parseInt(sc.nextLine()) - 1; } catch (Exception e) { return 0; }
    }
}