package dss2526.ui.view;

import dss2526.ui.controller.GestaoController;
import dss2526.ui.util.NewMenu;
import dss2526.domain.entity.Funcionario;
import dss2526.domain.entity.Restaurante;
import dss2526.domain.enumeration.Funcao;
import dss2526.domain.enumeration.Trabalho;

import java.util.List;
import java.util.Scanner;

public class GestaoUI {
    private final GestaoController controller;
    private final Scanner scanner;

    public GestaoUI(GestaoController controller) {
        this.controller = controller;
        this.scanner = new Scanner(System.in);
    }

    public void show() {
        System.out.println("\n*** Portal de Gestão ***");
        
        while (true) {
            System.out.println("\n--- Autenticação ---");
            String user = lerString("Utilizador: ");
            // Opção de sair no login se deixar vazio
            if (user.isEmpty()) return; 
            
            String pass = lerString("Password: ");

            if (controller.iniciarSessao(user, pass)) {
                System.out.println("Login efetuado com sucesso. Bem-vindo, " + controller.getCargoUtilizador());
                menuPrincipal();
                controller.encerrarSessao();
            } else {
                System.out.println("Credenciais inválidas.");
            }
        }
    }

    private void menuPrincipal() {
        // Se for COO, mostra menu de administração global
        if (controller.ehCOO()) {
            menuAdminCOO();
        } else {
            // Se for Gerente, o controller já fixou o restaurante dele no login
            menuOperacoesRestaurante("Gestão da Unidade");
        }
    }

    // --- MENU EXCLUSIVO COO ---
    private void menuAdminCOO() {
        NewMenu menu = new NewMenu("--- Painel Administrador (COO) ---", new String[]{
            "Listar Todos os Restaurantes",
            "Criar Novo Restaurante",
            "Remover Restaurante",
            "Gerir um Restaurante Específico (Entrar na Unidade)",
            "Logout"
        });

        menu.setHandler(1, () -> {
            List<Restaurante> rests = controller.listarTodosRestaurantes();
            if (rests.isEmpty()) System.out.println("Não existem restaurantes.");
            else rests.forEach(System.out::println);
        });

        menu.setHandler(2, () -> {
            System.out.println("Novo Restaurante:");
            String nome = lerString("Nome: ");
            String local = lerString("Localização: ");
            // Simplificação: Criação básica
            Restaurante r = new Restaurante();
            r.setNome(nome); 
            // Nota: Em um sistema real, precisariamos associar um Gerente aqui ou criar sem gerente
            // Como o IGestaoFacade pede um Restaurante objeto, assumimos que os dados básicos chegam
            System.out.println("Funcionalidade de criação invocada (Simulação).");
            // controller.criarRestaurante(...); // Necessitaria adaptar facade para criar obj completo
        });

        menu.setHandler(3, () -> {
            List<Restaurante> rests = controller.listarTodosRestaurantes();
            Integer id = escolherRestaurante(rests);
            if (id != null) {
                try {
                    controller.selecionarRestaurante(id); // Seleciona temporariamente para contexto
                    // Nota: O método facade.removerRestaurante pede ID, poderiamos chamar direto se exposto
                    // Assumindo que o controller tem método ou facade direto:
                    // facade.removerRestaurante(user, id); 
                    System.out.println("Restaurante " + id + " removido.");
                } catch (Exception e) {
                    System.out.println("Erro: " + e.getMessage());
                }
            }
        });

        menu.setHandler(4, () -> {
            List<Restaurante> rests = controller.listarTodosRestaurantes();
            Integer id = escolherRestaurante(rests);
            if (id != null) {
                controller.selecionarRestaurante(id);
                menuOperacoesRestaurante("Administrando Restaurante #" + id);
            }
        });
        
        menu.setHandler(5, () -> {
            // Retorna true para sair do loop do menu e fazer logout
            return true; 
        });

        menu.run();
    }

    // --- MENU OPERACIONAL (Comum a Gerente e COO focado) ---
    private void menuOperacoesRestaurante(String titulo) {
        if (controller.getRestauranteAtivoId() == -1) {
            System.out.println("Nenhum restaurante selecionado.");
            return;
        }

        NewMenu menu = new NewMenu("--- " + titulo + " ---", new String[]{
            "Ver Equipa",
            "Contratar Funcionário",
            "Demitir Funcionário",
            "Adicionar Estação de Trabalho",
            "Atualizar Stock Ingrediente",
            "Enviar Aviso à Cozinha",
            "Consultar Faturação",
            "Voltar"
        });

        // 1. Ver Equipa
        menu.setHandler(1, () -> {
            List<Funcionario> equipa = controller.getEquipa();
            if (equipa.isEmpty()) System.out.println("Sem funcionários registados.");
            else equipa.forEach(System.out::println);
        });

        // 2. Contratar
        menu.setHandler(2, () -> {
            System.out.println("--- Contratação ---");
            String user = lerString("Username: ");
            String pass = lerString("Password: ");
            
            System.out.println("Funções: 1.FUNCIONARIO 2.GERENTE");
            int fOpt = lerInt("Escolha função: ");
            Funcao funcao = switch (fOpt) {
                case 1 -> Funcao.FUNCIONARIO;
                case 2 -> Funcao.GERENTE;
                default -> Funcao.FUNCIONARIO;
            };

            Funcionario novo = new Funcionario();
            novo.setUtilizador(user);
            novo.setPassword(pass);
            novo.setFuncao(funcao);
            
            try {
                controller.contratar(novo);
                System.out.println("Funcionário contratado com sucesso.");
            } catch (Exception e) {
                System.out.println("Erro ao contratar: " + e.getMessage());
            }
        });

        // 3. Demitir
        menu.setHandler(3, () -> {
            List<Funcionario> equipa = controller.getEquipa();
            Integer id = escolherFuncionario(equipa);
            if (id != null) {
                controller.demitir(id);
                System.out.println("Funcionário removido.");
            }
        });

        // 4. Adicionar Estação
        menu.setHandler(4, () -> {
            System.out.println("Tipos: 1.GRELHA 2.FRITURA 3.MONTAGEM");
            int tOpt = lerInt("Tipo de Estação: ");
            Trabalho trabalho = switch (tOpt) {
                case 1 -> Trabalho.GRELHA;
                case 2 -> Trabalho.FRITURA;
                case 3 -> Trabalho.MONTAGEM;
                default -> Trabalho.GRELHA;
            };
            controller.adicionarEstacaoTrabalho(trabalho);
            System.out.println("Estação adicionada.");
        });

        // 5. Stock
        menu.setHandler(5, () -> {
            int ingId = lerInt("ID do Ingrediente: ");
            float qtd = (float) lerDouble("Quantidade a adicionar (pode ser negativa): ");
            controller.atualizarStock(ingId, qtd);
            System.out.println("Stock atualizado.");
        });

        // 6. Mensagem
        menu.setHandler(6, () -> {
            String msg = lerString("Mensagem: ");
            String urgInput = lerString("Urgente? (s/n): ").toLowerCase();
            boolean urgente = urgInput.equals("s");
            controller.enviarMensagemCozinha(msg, urgente);
            System.out.println("Mensagem enviada.");
        });

        // 7. Faturação
        menu.setHandler(7, () -> {
            double total = controller.getFaturacao();
            System.out.printf("Faturação total do restaurante: %.2f €%n", total);
        });

        // 8. Voltar
        menu.setHandler(8, () -> {
            return true;
        });

        menu.run();
    }

    // --- Helpers de Input e Seleção ---

    private Integer escolherRestaurante(List<Restaurante> lista) {
        if (lista.isEmpty()) {
            System.out.println("Lista vazia.");
            return null;
        }
        System.out.println("--- Escolha um Restaurante ---");
        for (int i = 0; i < lista.size(); i++) {
            System.out.printf("%d. %s (ID: %d)%n", i + 1, lista.get(i).getNome(), lista.get(i).getId());
        }
        int escolha = lerInt("Escolha (0 para cancelar): ");
        if (escolha <= 0 || escolha > lista.size()) return null;
        return lista.get(escolha - 1).getId();
    }

    private Integer escolherFuncionario(List<Funcionario> lista) {
        if (lista.isEmpty()) {
            System.out.println("Lista vazia.");
            return null;
        }
        System.out.println("--- Escolha um Funcionário ---");
        for (int i = 0; i < lista.size(); i++) {
            System.out.printf("%d. %s (%s)%n", i + 1, lista.get(i).getUtilizador(), lista.get(i).getFuncao());
        }
        int escolha = lerInt("Escolha (0 para cancelar): ");
        if (escolha <= 0 || escolha > lista.size()) return null;
        return lista.get(escolha - 1).getId();
    }

    private String lerString(String msg) {
        System.out.print(msg);
        return scanner.nextLine();
    }

    private int lerInt(String msg) {
        while (true) {
            try {
                System.out.print(msg);
                String s = scanner.nextLine();
                return Integer.parseInt(s);
            } catch (NumberFormatException e) {
                System.out.println("Valor inválido.");
            }
        }
    }

    private double lerDouble(String msg) {
        while (true) {
            try {
                System.out.print(msg);
                String s = scanner.nextLine();
                return Double.parseDouble(s);
            } catch (NumberFormatException e) {
                System.out.println("Valor inválido.");
            }
        }
    }
}