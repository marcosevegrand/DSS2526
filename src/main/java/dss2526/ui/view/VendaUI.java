package dss2526.ui.view;

import dss2526.ui.controller.VendaController;
import dss2526.ui.util.NewMenu;

import java.util.*;

public class VendaUI {
    private final VendaController ctrl = new VendaController();
    private final Scanner scanner = new Scanner(System.in);

    public void run() {
        if (!selecionarRestaurante()) {
            System.out.println("Nenhum restaurante disponível.");
            return;
        }

        NewMenu.builder("=== TERMINAL DE PEDIDOS ===")
                .addOption("Novo Pedido", this::fluxoNovoPedido)
                .addOption("Ver Pedidos Ativos", this::verPedidosAtivos)
                .run();
    }

    private boolean selecionarRestaurante() {
        List<String> restaurantes = ctrl.getRestaurantes();
        if (restaurantes.isEmpty()) return false;

        System.out.println("\n===== SELECIONAR RESTAURANTE =====");
        for (int i = 0; i < restaurantes.size(); i++) {
            System.out.println((i + 1) + ". " + restaurantes.get(i));
        }
        System.out.print("Escolha: ");

        try {
            int escolha = Integer.parseInt(scanner.nextLine().trim());
            if (escolha >= 1 && escolha <= restaurantes.size()) {
                ctrl.selecionarRestaurante(escolha - 1);
                return true;
            }
        } catch (NumberFormatException ignored) { }

        return false;
    }

    private boolean fluxoNovoPedido() {
        ctrl.iniciarNovoPedido();
        System.out.println("\n>> Novo pedido iniciado!");

        if (perguntarAlergenicos()) {
            System.out.println(">> Filtros de alergénicos aplicados.");
        }

        NewMenu menuPedido = new NewMenu("--- CONSTRUIR PEDIDO ---", List.of(
                "Adicionar Item", "Ver Carrinho", "Remover Item", "Finalizar Pedido", "Cancelar Pedido"
        ));

        menuPedido.setHandler(1, this::adicionarItem);
        menuPedido.setHandler(2, this::verCarrinho);
        menuPedido.setHandler(3, this::removerItem);

        // Exit menu only if finalizarPedido() returns true (pedido pago/confirmado)
        menuPedido.setHandler(4, this::finalizarPedido);

        // Cancel always exits this menu
        menuPedido.setHandler(5, () -> {
            ctrl.cancelar();
            System.out.println(">> Pedido cancelado.");
            return true;
        });

        menuPedido.run();

        // Return false so the main terminal menu keeps running
        return false;
    }

    private boolean perguntarAlergenicos() {
        System.out.print("\nDeseja filtrar alergénicos? (S/N): ");
        String resposta = scanner.nextLine().trim().toUpperCase();

        if (!resposta.equals("S")) return false;

        List<String> alergenicos = ctrl.getListaAlergenicos();
        if (alergenicos.isEmpty()) {
            System.out.println("Não existem alergénicos registados no sistema.");
            return false;
        }

        System.out.println("\n--- ALERGÉNICOS DISPONÍVEIS ---");
        for (int i = 0; i < alergenicos.size(); i++) {
            System.out.println((i + 1) + ". " + alergenicos.get(i));
        }
        System.out.println("Introduza os números separados por vírgula (ex: 1,3,5) ou 0 para nenhum:");
        System.out.print(">>> ");

        String input = scanner.nextLine().trim();
        if (input.equals("0") || input.isEmpty()) return false;

        List<String> selecionados = new ArrayList<>();
        try {
            for (String s : input.split(",")) {
                int idx = Integer.parseInt(s.trim()) - 1;
                if (idx >= 0 && idx < alergenicos.size()) {
                    selecionados.add(alergenicos.get(idx));
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida. Nenhum filtro aplicado.");
            return false;
        }

        ctrl.definirExclusoes(selecionados);
        return !selecionados.isEmpty();
    }

    private boolean adicionarItem() {
        List<String> catalogo = ctrl.getCatalogo();

        if (catalogo.isEmpty()) {
            System.out.println("\nNenhum item disponível no catálogo (verifique stock ou filtros).");
            return false;
        }

        System.out.println("\n--- CATÁLOGO DISPONÍVEL ---");
        for (int i = 0; i < catalogo.size(); i++) {
            System.out.println((i + 1) + ". " + catalogo.get(i));
        }

        System.out.print("\nEscolha o item (0 para voltar): ");
        try {
            int escolha = Integer.parseInt(scanner.nextLine().trim());
            if (escolha == 0) return false;
            if (escolha < 1 || escolha > catalogo.size()) {
                System.out.println("Opção inválida.");
                return false;
            }

            System.out.print("Quantidade: ");
            int qtd = Integer.parseInt(scanner.nextLine().trim());
            if (qtd <= 0) {
                System.out.println("Quantidade inválida.");
                return false;
            }

            System.out.print("Observação (Enter para nenhuma): ");
            String obs = scanner.nextLine().trim();

            if (ctrl.adicionarItem(escolha - 1, qtd, obs)) {
                System.out.println(">> Item adicionado ao pedido!");
            } else {
                System.out.println(">> Erro ao adicionar item.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida.");
        }

        return false;
    }

    private boolean verCarrinho() {
        List<String> linhas = ctrl.getLinhasCarrinho();

        System.out.println("\n--- CARRINHO ATUAL ---");
        if (linhas.isEmpty()) {
            System.out.println("(vazio)");
        } else {
            for (int i = 0; i < linhas.size(); i++) {
                System.out.println((i + 1) + ". " + linhas.get(i));
            }
        }

        return false;
    }

    private boolean removerItem() {
        List<String> linhas = ctrl.getLinhasCarrinho();

        if (linhas.isEmpty()) {
            System.out.println("\nCarrinho vazio.");
            return false;
        }

        System.out.println("\n--- REMOVER ITEM ---");
        for (int i = 0; i < linhas.size(); i++) {
            System.out.println((i + 1) + ". " + linhas.get(i));
        }

        System.out.print("Escolha o item a remover (0 para voltar): ");
        try {
            int escolha = Integer.parseInt(scanner.nextLine().trim());
            if (escolha == 0) return false;
            if (escolha < 1 || escolha > linhas.size()) {
                System.out.println("Opção inválida.");
                return false;
            }

            System.out.print("Quantidade a remover: ");
            int qtd = Integer.parseInt(scanner.nextLine().trim());

            ctrl.removerItem(escolha - 1, qtd);
            System.out.println(">> Item(s) removido(s).");
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida.");
        }

        return false;
    }

    private boolean finalizarPedido() {
        List<String> resumo = ctrl.getResumoDetalhado();

        if (resumo.size() <= 2) {
            System.out.println("\nO carrinho está vazio. Adicione itens antes de finalizar.");
            return false; // stay in menu
        }

        System.out.println("\n========== RESUMO DO PEDIDO ==========");
        resumo.forEach(System.out::println);
        System.out.println("=======================================");

        System.out.print("\nConfirmar pedido? (S/N): ");
        String confirma = scanner.nextLine().trim().toUpperCase();

        if (!confirma.equals("S")) {
            System.out.println(">> Pedido não confirmado. Volte ao menu para continuar a editar.");
            return false; // stay in menu
        }

        System.out.println("\n--- MÉTODO DE PAGAMENTO ---");
        System.out.println("1. Pagar no Terminal (imediato)");
        System.out.println("2. Pagar na Caixa (balcão)");
        System.out.print("Escolha: ");

        try {
            int opcao = Integer.parseInt(scanner.nextLine().trim());
            if (opcao != 1 && opcao != 2) {
                System.out.println("Opção inválida.");
                return false; // stay in menu
            }

            String resultado = ctrl.pagar(opcao);
            System.out.println("\n========================================");
            System.out.println(resultado);
            System.out.println("========================================");
            System.out.println("\nObrigado pela preferência!");

            return true; // exit menuPedido
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida.");
            return false; // stay in menu
        }
    }

    private boolean verPedidosAtivos() {
        List<String> pedidos = ctrl.getEcraPedidosAtivos();

        System.out.println("\n===== PEDIDOS ATIVOS =====");
        if (pedidos.isEmpty()) {
            System.out.println("Nenhum pedido ativo no momento.");
        } else {
            pedidos.forEach(System.out::println);
        }

        return false;
    }
}
