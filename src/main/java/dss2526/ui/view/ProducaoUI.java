package dss2526.ui.view;

import dss2526.ui.controller.ProducaoController;
import dss2526.ui.util.NewMenu;

import java.util.*;

/**
 * Interface de utilizador para o módulo de Produção (Cozinha/Caixa).
 * Implementa os use cases: Levantar Pedido, Consultar Fila de Pedidos, Preparar Pedido
 */
public class ProducaoUI {
    private final ProducaoController ctrl = new ProducaoController();
    private final Scanner scanner = new Scanner(System.in);

    public void run() {
        // 1. Selecionar Restaurante
        if (!selecionarRestaurante()) {
            System.out.println("Nenhum restaurante disponível.");
            return;
        }

        // 2. Selecionar Estação de Trabalho
        if (!selecionarEstacao()) {
            System.out.println("Nenhuma estação disponível neste restaurante.");
            return;
        }

        // 3. Menu baseado no tipo de estação
        if (ctrl.isCaixa()) {
            menuCaixa();
        } else {
            menuCozinha();
        }
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
        } catch (NumberFormatException ignored) {}

        return false;
    }

    private boolean selecionarEstacao() {
        List<String> estacoes = ctrl.getEstacoes();
        if (estacoes.isEmpty()) return false;

        System.out.println("\n===== SELECIONAR ESTAÇÃO DE TRABALHO =====");
        for (int i = 0; i < estacoes.size(); i++) {
            System.out.println((i + 1) + ". " + estacoes.get(i));
        }
        System.out.print("Escolha: ");

        try {
            int escolha = Integer.parseInt(scanner.nextLine().trim());
            if (escolha >= 1 && escolha <= estacoes.size()) {
                ctrl.selecionarEstacao(escolha - 1);
                System.out.println("\n>> Bem-vindo à estação: " + ctrl.getNomeEstacao());
                return true;
            }
        } catch (NumberFormatException ignored) {}

        return false;
    }

    // ==================== MENU COZINHA ====================

    private void menuCozinha() {
        NewMenu.builder("=== ESTAÇÃO: " + ctrl.getNomeEstacao() + " (COZINHA) ===")
                .addOption("Ver Tarefas Pendentes", this::verTarefasPendentes)
                .addOption("Iniciar Tarefa", this::iniciarTarefa)
                .addOption("Ver Tarefas em Execução", this::verTarefasEmExecucao)
                .addOption("Concluir Tarefa", this::concluirTarefa)
                .addOption("Reportar Atraso (Falta de Ingrediente)", this::reportarAtraso)
                .addOption("Monitor Global de Pedidos", this::monitorGlobal)
                .addOption("Ver Mensagens", this::verMensagens)
                .run();
    }

    private boolean verTarefasPendentes() {
        List<String> tarefas = ctrl.getTarefasPendentes();

        System.out.println("\n--- TAREFAS PENDENTES (disponíveis para esta estação) ---");
        if (tarefas.isEmpty()) {
            System.out.println("Nenhuma tarefa pendente disponível.");
        } else {
            for (int i = 0; i < tarefas.size(); i++) {
                System.out.println((i + 1) + ". " + tarefas.get(i));
            }
        }

        return false;
    }

    private boolean iniciarTarefa() {
        List<String> tarefas = ctrl.getTarefasPendentes();

        if (tarefas.isEmpty()) {
            System.out.println("\nNenhuma tarefa pendente disponível para iniciar.");
            return false;
        }

        System.out.println("\n--- INICIAR TAREFA ---");
        for (int i = 0; i < tarefas.size(); i++) {
            System.out.println((i + 1) + ". " + tarefas.get(i));
        }
        System.out.print("Escolha a tarefa a iniciar (0 para voltar): ");

        try {
            int escolha = Integer.parseInt(scanner.nextLine().trim());
            if (escolha == 0) return false;
            if (escolha >= 1 && escolha <= tarefas.size()) {
                ctrl.iniciarTarefaPendente(escolha - 1);
                System.out.println(">> Tarefa iniciada!");
            } else {
                System.out.println("Opção inválida.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida.");
        }

        return false;
    }

    private boolean verTarefasEmExecucao() {
        List<String> tarefas = ctrl.getTarefasEmExecucao();

        System.out.println("\n--- TAREFAS EM EXECUÇÃO (nesta estação) ---");
        if (tarefas.isEmpty()) {
            System.out.println("Nenhuma tarefa em execução.");
        } else {
            for (int i = 0; i < tarefas.size(); i++) {
                System.out.println((i + 1) + ". " + tarefas.get(i));
            }
        }

        return false;
    }

    private boolean concluirTarefa() {
        List<String> tarefas = ctrl.getTarefasEmExecucao();

        if (tarefas.isEmpty()) {
            System.out.println("\nNenhuma tarefa em execução para concluir.");
            return false;
        }

        System.out.println("\n--- CONCLUIR TAREFA ---");
        for (int i = 0; i < tarefas.size(); i++) {
            System.out.println((i + 1) + ". " + tarefas.get(i));
        }
        System.out.print("Escolha a tarefa a concluir (0 para voltar): ");

        try {
            int escolha = Integer.parseInt(scanner.nextLine().trim());
            if (escolha == 0) return false;
            if (escolha >= 1 && escolha <= tarefas.size()) {
                ctrl.concluirTarefaEmExecucao(escolha - 1);
                System.out.println(">> Tarefa concluída!");
            } else {
                System.out.println("Opção inválida.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida.");
        }

        return false;
    }

    private boolean reportarAtraso() {
        List<String> tarefas = ctrl.getTarefasEmExecucao();

        if (tarefas.isEmpty()) {
            System.out.println("\nNenhuma tarefa em execução para reportar atraso.");
            return false;
        }

        System.out.println("\n--- REPORTAR ATRASO ---");
        for (int i = 0; i < tarefas.size(); i++) {
            System.out.println((i + 1) + ". " + tarefas.get(i));
        }
        System.out.print("Escolha a tarefa (0 para voltar): ");

        try {
            int escolhaTarefa = Integer.parseInt(scanner.nextLine().trim());
            if (escolhaTarefa == 0) return false;
            if (escolhaTarefa < 1 || escolhaTarefa > tarefas.size()) {
                System.out.println("Opção inválida.");
                return false;
            }

            // Mostrar ingredientes da tarefa
            List<String> ingredientes = ctrl.getIngredientesDaTarefaEmExecucao(escolhaTarefa - 1);

            if (ingredientes.isEmpty()) {
                System.out.println("Esta tarefa não tem ingredientes registados.");
                return false;
            }

            System.out.println("\n--- INGREDIENTE EM FALTA ---");
            for (int i = 0; i < ingredientes.size(); i++) {
                System.out.println((i + 1) + ". " + ingredientes.get(i));
            }
            System.out.print("Escolha o ingrediente em falta: ");

            int escolhaIng = Integer.parseInt(scanner.nextLine().trim());
            if (escolhaIng >= 1 && escolhaIng <= ingredientes.size()) {
                ctrl.atrasarTarefaEmExecucao(escolhaTarefa - 1, escolhaIng - 1);
                System.out.println(">> Atraso reportado! Mensagem enviada ao sistema.");
            } else {
                System.out.println("Opção inválida.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida.");
        }

        return false;
    }

    // ==================== MENU CAIXA ====================

    private void menuCaixa() {
        NewMenu.builder("=== ESTAÇÃO: " + ctrl.getNomeEstacao() + " (CAIXA) ===")
                .addOption("Processar Pagamentos Pendentes", this::processarPagamentos)
                .addOption("Entregar Pedidos Prontos", this::entregarPedidos)
                .addOption("Solicitar Refação de Itens", this::solicitarRefacao)
                .addOption("Monitor Global de Pedidos", this::monitorGlobal)
                .addOption("Ver Mensagens", this::verMensagens)
                .run();
    }

    private boolean processarPagamentos() {
        List<String> pedidos = ctrl.getPedidosNaoPagos();

        if (pedidos.isEmpty()) {
            System.out.println("\nNenhum pedido aguardando pagamento.");
            return false;
        }

        System.out.println("\n--- PEDIDOS AGUARDANDO PAGAMENTO ---");
        for (int i = 0; i < pedidos.size(); i++) {
            System.out.println((i + 1) + ". " + pedidos.get(i));
        }
        System.out.print("Escolha o pedido a processar (0 para voltar): ");

        try {
            int escolha = Integer.parseInt(scanner.nextLine().trim());
            if (escolha == 0) return false;
            if (escolha >= 1 && escolha <= pedidos.size()) {
                String resultado = ctrl.confirmarPagamento(escolha - 1);
                System.out.println("\n>> " + resultado);
            } else {
                System.out.println("Opção inválida.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida.");
        }

        return false;
    }

    private boolean entregarPedidos() {
        List<String> pedidos = ctrl.getPedidosProntos();

        if (pedidos.isEmpty()) {
            System.out.println("\nNenhum pedido pronto para entrega.");
            return false;
        }

        System.out.println("\n--- PEDIDOS PRONTOS PARA ENTREGA ---");
        for (int i = 0; i < pedidos.size(); i++) {
            System.out.println((i + 1) + ". " + pedidos.get(i));
        }
        System.out.print("Escolha o pedido a entregar (0 para voltar): ");

        try {
            int escolha = Integer.parseInt(scanner.nextLine().trim());
            if (escolha == 0) return false;
            if (escolha >= 1 && escolha <= pedidos.size()) {
                ctrl.entregarPedido(escolha - 1);
                System.out.println(">> Pedido entregue ao cliente!");
            } else {
                System.out.println("Opção inválida.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida.");
        }

        return false;
    }

    private boolean solicitarRefacao() {
        List<String> pedidos = ctrl.getPedidosProntos();

        if (pedidos.isEmpty()) {
            System.out.println("\nNenhum pedido pronto disponível para refação.");
            return false;
        }

        System.out.println("\n--- SOLICITAR REFAÇÃO (Devolução de Cliente) ---");
        for (int i = 0; i < pedidos.size(); i++) {
            System.out.println((i + 1) + ". " + pedidos.get(i));
        }
        System.out.print("Escolha o pedido (0 para voltar): ");

        try {
            int escolhaPedido = Integer.parseInt(scanner.nextLine().trim());
            if (escolhaPedido == 0) return false;
            if (escolhaPedido < 1 || escolhaPedido > pedidos.size()) {
                System.out.println("Opção inválida.");
                return false;
            }

            // Mostrar itens do pedido
            List<String> linhas = ctrl.getLinhasPedido(escolhaPedido - 1);

            if (linhas.isEmpty()) {
                System.out.println("Este pedido não tem itens.");
                return false;
            }

            System.out.println("\n--- ITENS DO PEDIDO ---");
            for (int i = 0; i < linhas.size(); i++) {
                System.out.println((i + 1) + ". " + linhas.get(i));
            }
            System.out.println("\nIntroduza os números dos itens a refazer, separados por vírgula:");
            System.out.print(">>> ");

            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("Nenhum item selecionado.");
                return false;
            }

            List<Integer> indices = new ArrayList<>();
            for (String s : input.split(",")) {
                try {
                    int idx = Integer.parseInt(s.trim()) - 1;
                    if (idx >= 0 && idx < linhas.size()) {
                        indices.add(idx);
                    }
                } catch (NumberFormatException ignored) {}
            }

            if (indices.isEmpty()) {
                System.out.println("Nenhum item válido selecionado.");
                return false;
            }

            ctrl.solicitarRefacao(escolhaPedido - 1, indices);
            System.out.println(">> Refação solicitada! Novas tarefas criadas na cozinha.");
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida.");
        }

        return false;
    }

    // ==================== FUNCIONALIDADES COMUNS ====================

    private boolean monitorGlobal() {
        List<String> monitor = ctrl.getMonitorGlobal();

        System.out.println("\n========== MONITOR DE PEDIDOS ==========");
        if (monitor.isEmpty()) {
            System.out.println("Nenhum pedido em processamento.");
        } else {
            monitor.forEach(System.out::println);
        }
        System.out.println("=========================================");

        return false;
    }

    private boolean verMensagens() {
        List<String> mensagens = ctrl.getMensagens();

        System.out.println("\n========== MENSAGENS DO SISTEMA ==========");
        if (mensagens.isEmpty()) {
            System.out.println("Nenhuma mensagem.");
        } else {
            mensagens.forEach(System.out::println);
        }
        System.out.println("==========================================");

        return false;
    }
}