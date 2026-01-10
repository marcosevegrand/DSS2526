package dss2526.ui.view;

import dss2526.domain.enumeration.Funcao;
import dss2526.ui.controller.GestaoController;
import dss2526.ui.util.NewMenu;

import java.util.*;

public class GestaoUI {
    private final GestaoController ctrl = new GestaoController();
    private final Scanner scanner = new Scanner(System.in);

    public void run() {
        if (!autenticar()) {
            System.out.println("Autenticação falhou. Acesso negado.");
            return;
        }

        System.out.println("\n>> Bem-vindo, " + ctrl.getNomeUtilizador() + "!");

        if (ctrl.isCOO()) {
            menuCOO();          // COO starts in a global menu (no restaurant context)
        } else {
            // Manager must NOT choose a restaurant: controller should already have set it on login.
            if (!ctrl.temRestauranteSelecionado()) {
                System.out.println("\nErro: gerente sem restaurante atribuído.");
                return;
            }
            menuGerente();      // Directly goes to manager options
        }
    }

    private boolean autenticar() {
        System.out.println("\n========== SISTEMA DE GESTÃO ==========");
        System.out.println("Acesso restrito a Gerentes e COO.");

        for (int tentativas = 0; tentativas < 3; tentativas++) {
            System.out.print("\nUtilizador: ");
            String user = scanner.nextLine().trim();
            System.out.print("Password: ");
            String pass = scanner.nextLine().trim();

            if (ctrl.autenticar(user, pass)) {
                return true;
            }

            System.out.println("Credenciais inválidas. Tentativas restantes: " + (2 - tentativas));
        }

        return false;
    }

    // ==================== COO FLOW ====================

    /**
     * COO menu is GLOBAL (no restaurant actions here).
     * From here, COO can either pick a restaurant (enter local management) or send a global message.
     */
    private void menuCOO() {
        // Ensure COO starts without a selected restaurant context
        ctrl.limparRestauranteAtual();

        NewMenu.builder("=== PAINEL COO ===")
                .addOption("Selecionar Restaurante", this::cooSelecionarRestauranteEntrar)
                .addOption("Difundir Mensagem Global", this::difundirMensagemGlobal)
                .run();
    }

    /**
     * COO selects a restaurant and then enters the same menu as a manager.
     * When they exit that menu (0), they return to the COO global menu.
     */
    private boolean cooSelecionarRestauranteEntrar() {
        boolean ok = selecionarRestaurante();
        if (!ok || !ctrl.temRestauranteSelecionado()) return false;

        menuGerenteCOO();   // same options as manager, plus "Trocar restaurante"
        return false;       // return to COO global menu afterwards
    }

    // ==================== MANAGER MENUS ====================

    /**
     * Pure manager menu: no "select" or "swap restaurant".
     */
    private void menuGerente() {
        NewMenu.builder("=== PAINEL DE GESTÃO ===")
                .addOption("Consultar Estatísticas", this::consultarEstatisticas)
                .addOption("Gerir Stock", this::gerirStock)
                .addOption("Gerir Funcionários", this::gerirFuncionarios)
                .addOption("Gerir Estações", this::gerirEstacoes)
                .addOption("Enviar Mensagem", this::enviarMensagemLocal)
                .run();
    }

    /**
     * COO local management menu: same as manager, but can swap restaurant (exit to selection).
     * This avoids having both "Selecionar Restaurante" and "Trocar de Restaurante" in the global COO menu.
     */
    private void menuGerenteCOO() {
        NewMenu.builder("=== PAINEL DE GESTÃO (COO) ===")
                .addOption("Consultar Estatísticas", this::consultarEstatisticas)
                .addOption("Gerir Stock", this::gerirStock)
                .addOption("Gerir Funcionários", this::gerirFuncionarios)
                .addOption("Gerir Estações", this::gerirEstacoes)
                .addOption("Enviar Mensagem", this::enviarMensagemLocal)
                .addOption("Trocar de Restaurante", () -> {
                    ctrl.limparRestauranteAtual();
                    // returning true exits this local menu; COO then can select again from global menu
                    return true;
                })
                .run();
    }

    // ==================== OPERATIONS ====================

    /**
     * NOTE: now returns true when a restaurant is selected, false otherwise.
     */
    private boolean selecionarRestaurante() {
        List<String> restaurantes = ctrl.getRestaurantes();

        if (restaurantes.isEmpty()) {
            System.out.println("\nNenhum restaurante disponível.");
            return false;
        }

        System.out.println("\n--- SELECIONAR RESTAURANTE ---");
        for (int i = 0; i < restaurantes.size(); i++) {
            System.out.println((i + 1) + ". " + restaurantes.get(i));
        }
        System.out.print("Escolha (0 para voltar): ");

        try {
            int escolha = Integer.parseInt(scanner.nextLine().trim());
            if (escolha == 0) return false;

            if (escolha >= 1 && escolha <= restaurantes.size()) {
                ctrl.definirRestauranteAtual(escolha - 1);
                System.out.println(">> Restaurante selecionado!");
                return true;
            }
        } catch (NumberFormatException ignored) { }

        System.out.println("Opção inválida.");
        return false;
    }

    private boolean consultarEstatisticas() {
        if (!ctrl.temRestauranteSelecionado()) {
            System.out.println("\nPor favor, selecione um restaurante primeiro.");
            return false;
        }

        System.out.println("\n--- CONSULTAR ESTATÍSTICAS ---");
        System.out.println("Introduza o período para análise (formato: AAAA-MM-DD)");
        System.out.println("(Deixe em branco para todo o histórico)");

        System.out.print("Data início: ");
        String inicio = scanner.nextLine().trim();

        System.out.print("Data fim: ");
        String fim = scanner.nextLine().trim();

        String dashboard = ctrl.obterDashboard(
                inicio.isEmpty() ? null : inicio,
                fim.isEmpty() ? null : fim
        );

        System.out.println("\n" + dashboard);
        return false;
    }

    private boolean gerirStock() {
        if (!ctrl.temRestauranteSelecionado()) {
            System.out.println("\nPor favor, selecione um restaurante primeiro.");
            return false;
        }

        List<String> ingredientes = ctrl.getIngredientes();

        if (ingredientes.isEmpty()) {
            System.out.println("\nNenhum ingrediente registado no sistema.");
            return false;
        }

        System.out.println("\n--- GESTÃO DE STOCK ---");
        for (int i = 0; i < ingredientes.size(); i++) {
            System.out.println((i + 1) + ". " + ingredientes.get(i));
        }
        System.out.print("Escolha o ingrediente (0 para voltar): ");

        try {
            int escolha = Integer.parseInt(scanner.nextLine().trim());
            if (escolha == 0) return false;
            if (escolha < 1 || escolha > ingredientes.size()) {
                System.out.println("Opção inválida.");
                return false;
            }

            System.out.print("Quantidade a adicionar/remover (ex: +10 ou -5): ");
            String deltaStr = scanner.nextLine().trim();

            int delta;
            if (deltaStr.startsWith("+")) delta = Integer.parseInt(deltaStr.substring(1));
            else delta = Integer.parseInt(deltaStr);

            ctrl.atualizarStock(escolha - 1, delta);
            System.out.println(">> Stock atualizado!");
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida.");
        }

        return false;
    }

    private boolean gerirFuncionarios() {
        if (!ctrl.temRestauranteSelecionado()) {
            System.out.println("\nPor favor, selecione um restaurante primeiro.");
            return false;
        }

        NewMenu.builder("--- GESTÃO DE FUNCIONÁRIOS ---")
                .addOption("Listar Funcionários", this::listarFuncionarios)
                .addOption("Contratar Funcionário", this::contratarFuncionario)
                .addOption("Demitir Funcionário", this::demitirFuncionario)
                .run();

        return false;
    }

    private boolean listarFuncionarios() {
        List<String> funcionarios = ctrl.getNomesFuncionarios();

        System.out.println("\n--- FUNCIONÁRIOS DO RESTAURANTE ---");
        if (funcionarios.isEmpty()) System.out.println("Nenhum funcionário (além de você).");
        else for (int i = 0; i < funcionarios.size(); i++) System.out.println((i + 1) + ". " + funcionarios.get(i));

        return false;
    }

    private boolean contratarFuncionario() {
        System.out.println("\n--- CONTRATAR FUNCIONÁRIO ---");

        System.out.print("Nome de utilizador: ");
        String user = scanner.nextLine().trim();
        if (user.isEmpty()) {
            System.out.println("Nome de utilizador não pode ser vazio.");
            return false;
        }

        System.out.print("Password: ");
        String pass = scanner.nextLine().trim();
        if (pass.isEmpty()) {
            System.out.println("Password não pode ser vazia.");
            return false;
        }

        System.out.println("Função:");
        System.out.println("1. FUNCIONARIO");
        System.out.println("2. GERENTE");
        System.out.print("Escolha: ");

        try {
            int funcaoIdx = Integer.parseInt(scanner.nextLine().trim());
            Funcao funcao;
            switch (funcaoIdx) {
                case 1: funcao = Funcao.FUNCIONARIO; break;
                case 2: funcao = Funcao.GERENTE; break;
                default:
                    System.out.println("Opção inválida.");
                    return false;
            }

            ctrl.contratarFuncionario(user, pass, funcao);
            System.out.println(">> Funcionário contratado com sucesso!");
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida.");
        }

        return false;
    }

    private boolean demitirFuncionario() {
        List<String> funcionarios = ctrl.getNomesFuncionarios();

        if (funcionarios.isEmpty()) {
            System.out.println("\nNenhum funcionário para demitir.");
            return false;
        }

        System.out.println("\n--- DEMITIR FUNCIONÁRIO ---");
        for (int i = 0; i < funcionarios.size(); i++) {
            System.out.println((i + 1) + ". " + funcionarios.get(i));
        }
        System.out.print("Escolha o funcionário (0 para voltar): ");

        try {
            int escolha = Integer.parseInt(scanner.nextLine().trim());
            if (escolha == 0) return false;

            if (escolha >= 1 && escolha <= funcionarios.size()) {
                System.out.print("Confirmar demissão? (S/N): ");
                if (scanner.nextLine().trim().toUpperCase().equals("S")) {
                    ctrl.demitirFuncionario(escolha - 1);
                    System.out.println(">> Funcionário demitido.");
                }
            } else {
                System.out.println("Opção inválida.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida.");
        }

        return false;
    }

    private boolean gerirEstacoes() {
        if (!ctrl.temRestauranteSelecionado()) {
            System.out.println("\nPor favor, selecione um restaurante primeiro.");
            return false;
        }

        NewMenu.builder("--- GESTÃO DE ESTAÇÕES ---")
                .addOption("Listar Estações", this::listarEstacoes)
                .addOption("Adicionar Estação", this::adicionarEstacao)
                .addOption("Remover Estação", this::removerEstacao)
                .run();

        return false;
    }

    private boolean listarEstacoes() {
        List<String> estacoes = ctrl.getNomesEstacoes();

        System.out.println("\n--- ESTAÇÕES DO RESTAURANTE ---");
        if (estacoes.isEmpty()) System.out.println("Nenhuma estação registada.");
        else for (int i = 0; i < estacoes.size(); i++) System.out.println((i + 1) + ". " + estacoes.get(i));

        return false;
    }

    private boolean adicionarEstacao() {
        System.out.println("\n--- ADICIONAR ESTAÇÃO ---");

        System.out.print("Nome da estação: ");
        String nome = scanner.nextLine().trim();
        if (nome.isEmpty()) {
            System.out.println("Nome não pode ser vazio.");
            return false;
        }

        System.out.println("Tipo de estação:");
        System.out.println("1. Cozinha");
        System.out.println("2. Caixa");
        System.out.print("Escolha: ");

        try {
            int tipo = Integer.parseInt(scanner.nextLine().trim());
            if (tipo != 1 && tipo != 2) {
                System.out.println("Opção inválida.");
                return false;
            }

            ctrl.adicionarEstacao(nome, tipo == 2);
            System.out.println(">> Estação adicionada com sucesso!");
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida.");
        }

        return false;
    }

    private boolean removerEstacao() {
        List<String> estacoes = ctrl.getNomesEstacoes();

        if (estacoes.isEmpty()) {
            System.out.println("\nNenhuma estação para remover.");
            return false;
        }

        System.out.println("\n--- REMOVER ESTAÇÃO ---");
        for (int i = 0; i < estacoes.size(); i++) {
            System.out.println((i + 1) + ". " + estacoes.get(i));
        }
        System.out.print("Escolha a estação (0 para voltar): ");

        try {
            int escolha = Integer.parseInt(scanner.nextLine().trim());
            if (escolha == 0) return false;

            if (escolha >= 1 && escolha <= estacoes.size()) {
                System.out.print("Confirmar remoção? (S/N): ");
                if (scanner.nextLine().trim().toUpperCase().equals("S")) {
                    ctrl.removerEstacao(escolha - 1);
                    System.out.println(">> Estação removida.");
                }
            } else {
                System.out.println("Opção inválida.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida.");
        }

        return false;
    }

    private boolean enviarMensagemLocal() {
        if (!ctrl.temRestauranteSelecionado()) {
            System.out.println("\nPor favor, selecione um restaurante primeiro.");
            return false;
        }

        System.out.print("\nMensagem para o restaurante: ");
        String texto = scanner.nextLine().trim();

        if (!texto.isEmpty()) {
            ctrl.enviarMensagemLocal(texto);
            System.out.println(">> Mensagem enviada!");
        }

        return false;
    }

    private boolean difundirMensagemGlobal() {
        System.out.print("\nMensagem para TODOS os restaurantes: ");
        String texto = scanner.nextLine().trim();

        if (!texto.isEmpty()) {
            ctrl.enviarMensagemGlobal(texto);
            System.out.println(">> Mensagem difundida para todos os restaurantes!");
        }

        return false;
    }
}
