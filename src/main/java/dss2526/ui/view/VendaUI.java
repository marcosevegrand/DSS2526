package dss2526.ui.view;

import dss2526.ui.controller.VendaController;
import dss2526.ui.util.NewMenu;
import java.util.*;

public class VendaUI {
    private final VendaController controller = new VendaController();
    private final Scanner sc = new Scanner(System.in);

    public void run() {
        List<String> rests = controller.getRestaurantes();
        int restIdx = pick("SELECIONE O RESTAURANTE", rests);
        if (restIdx == -1) return; 
        
        controller.selecionarRestaurante(restIdx);

        NewMenu.builder("MODO VENDA")
            .addOption("Iniciar Novo Pedido", this::fluxoNovoPedido)
            .addOption("Consultar Pedidos Ativos", this::fluxoConsultarAtivos)
            .run();
    }

    private boolean fluxoNovoPedido() {
        controller.iniciarNovoPedido();

        // 1. Filtragem de Alergénios
        List<String> alergNames = controller.getListaAlergenicos();
        if (!alergNames.isEmpty()) {
            System.out.println("\n--- ALERGÉNIOS DETECTADOS ---");
            for (int i = 0; i < alergNames.size(); i++) System.out.println((i + 1) + ". " + alergNames.get(i));
            System.out.print("Números a excluir (ex: 1 3) ou ENTER para nenhum: ");
            String input = sc.nextLine();
            if (!input.isBlank()) {
                try {
                    List<String> selecionados = Arrays.stream(input.split(" "))
                        .map(String::trim).filter(s -> !s.isEmpty())
                        .map(s -> Integer.parseInt(s) - 1)
                        .filter(i -> i >= 0 && i < alergNames.size())
                        .map(alergNames::get).toList();
                    controller.definirExclusoes(selecionados);
                } catch (Exception e) { System.out.println("Filtros ignorados devido a entrada inválida."); }
            }
        }

        // Sub-menu de Gestão do Pedido
        NewMenu.builder("GESTÃO DO PEDIDO")
            .addOption("Adicionar Item", this::adicionarItem)
            .addOption("Remover Item", this::removerItem)
            .addOption("Finalizar Pedido", this::finalizarPedido)
            .addOption("Sair / Abortar", () -> { controller.cancelar(); return true; })
            .run();
        return false;
    }

    private boolean adicionarItem() {
        List<String> cat = controller.getCatalogo();
        if (cat.isEmpty()) {
            System.out.println("Nenhum item disponível com os filtros/stock atuais.");
            return false;
        }
        int idx = pick("CATÁLOGO", cat);
        if (idx == -1) return false; 

        try {
            System.out.print("Quantidade: ");
            int qtd = Integer.parseInt(sc.nextLine().trim());
            if (qtd <= 0) { System.out.println("Quantidade deve ser superior a 0."); return false; }
            System.out.print("Observação: ");
            String obs = sc.nextLine();
            if (controller.adicionarItem(idx, qtd, obs)) System.out.println("Item adicionado.");
        } catch (Exception e) { System.out.println("Entrada inválida."); }
        return false;
    }

    private boolean removerItem() {
        List<String> cart = controller.getLinhasCarrinho();
        if (cart.isEmpty()) { System.out.println("O carrinho está vazio."); return false; }
        int idx = pick("REMOVER ITEM", cart);
        if (idx == -1) return false;

        try {
            System.out.print("Quantidade a remover: ");
            int q = Integer.parseInt(sc.nextLine().trim());
            controller.removerItem(idx, q);
        } catch (Exception e) { System.out.println("Valor inválido."); }
        return false;
    }

    private boolean finalizarPedido() {
        System.out.println("\n" + controller.getResumoPedido());
        System.out.print("Deseja confirmar a finalização? (S/N): ");
        if (sc.nextLine().equalsIgnoreCase("s")) {
            int pay = pick("OPÇÃO DE PAGAMENTO", List.of("TERMINAL (Direto)", "CAIXA (Balcão)"));
            if (pay == -1) return false;
            controller.pagar(pay == 0 ? 1 : 2);
            System.out.println(pay == 0 ? "PEDIDO CONFIRMADO." : "PEDIDO ENVIADO. AGUARDA PAGAMENTO NA CAIXA.");
            return true;
        }
        return false;
    }

    private boolean fluxoConsultarAtivos() {
        List<String> ativos = controller.getEcraPedidosAtivos();
        System.out.println("\n--- PEDIDOS EM PROCESSAMENTO ---");
        if (ativos.isEmpty()) System.out.println("Nenhum pedido ativo.");
        else ativos.forEach(System.out::println);
        return false;
    }

    private int pick(String t, List<String> l) {
        System.out.println("\n--- " + t + " ---");
        for (int i = 0; i < l.size(); i++) System.out.println((i + 1) + ". " + l.get(i));
        System.out.println("0. Voltar Atrás");
        System.out.print("Seleção > ");
        try {
            int ch = Integer.parseInt(sc.nextLine().trim());
            return (ch > 0 && ch <= l.size()) ? ch - 1 : -1;
        } catch (Exception e) { return -1; }
    }
}