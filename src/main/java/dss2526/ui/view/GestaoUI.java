package dss2526.ui.view;

import dss2526.ui.controller.GestaoController;
import dss2526.ui.util.NewMenu;
import dss2526.domain.enumeration.Funcao;
import java.util.*;

public class GestaoUI {
    private final GestaoController c = new GestaoController();
    private final Scanner sc = new Scanner(System.in);

    public void run() {
        System.out.println("\n=== LOGIN GESTÃO ===");
        System.out.print("Utilizador: "); String u = sc.nextLine();
        System.out.print("Senha: "); String p = sc.nextLine();
        
        if(!c.autenticar(u, p)) { 
            System.out.println("Acesso negado ou credenciais inválidas."); 
            return; 
        }

        System.out.println("Bem-vindo, " + c.getNomeUtilizador() + ".");

        if (c.isCOO()) {
            menuPrincipalCOO();
        } else {
            menuGestaoRestaurante();
        }
    }

    private void menuPrincipalCOO() {
        boolean sair = false;
        while (!sair) {
            c.limparRestauranteAtual();
            
            System.out.println("\n=== PAINEL COO ===");
            System.out.println("1. Gerir um Restaurante (Selecionar)");
            System.out.println("2. Enviar Mensagem Global (Broadcast)");
            System.out.println("0. Sair");
            System.out.print("Opção > ");
            
            String op = sc.nextLine();
            switch (op) {
                case "1":
                    List<String> rests = c.getRestaurantes();
                    int idx = pick("Selecione o Restaurante", rests);
                    if (idx != -1) {
                        c.definirRestauranteAtual(idx);
                        menuGestaoRestaurante();
                    }
                    break;
                case "2":
                    System.out.print("Mensagem para TODA a rede: ");
                    String msg = sc.nextLine();
                    if (!msg.isBlank()) {
                        c.enviarMensagemGlobal(msg);
                        System.out.println("Mensagem difundida.");
                    }
                    break;
                case "0": sair = true; break;
                default: System.out.println("Opção inválida.");
            }
        }
    }

    private void menuGestaoRestaurante() {
        if (!c.temRestauranteSelecionado()) {
            System.out.println("Erro: Nenhum restaurante selecionado.");
            return;
        }

        NewMenu.builder("GESTÃO DE RESTAURANTE")
            .style(NewMenu.MenuStyle.NUMBERED)
            .addOption("Recursos Humanos", this::menuRH)
            .addOption("Gestão de Inventário (Stock)", this::menuStock)
            .addOption("Gestão de Estações/Equipamento", this::menuEstacoes)
            .addOption("Dashboard / Estatísticas", this::menuDashboard) // Atualizado para chamar submenu
            .addOption("Enviar Mensagem Local", () -> {
                System.out.print("Texto da mensagem: ");
                String t = sc.nextLine();
                if(!t.isBlank()) {
                    c.enviarMensagemLocal(t);
                    System.out.println("Mensagem enviada para a equipa local.");
                }
                return false;
            })
            .run();
    }

    // --- Submenus ---

    private boolean menuDashboard() {
        System.out.println("\n--- DASHBOARD ESTATÍSTICO ---");
        System.out.println("1. Todo o Histórico");
        System.out.println("2. Intervalo Específico");
        System.out.println("0. Voltar");
        System.out.print("Opção > ");
        
        String op = sc.nextLine();
        
        if (op.equals("1")) {
            System.out.println("\n" + c.obterDashboard(null, null));
        } else if (op.equals("2")) {
            System.out.println("Formato: AAAA-MM-DD (ex: 2025-01-31)");
            System.out.print("Data Início: "); String inicio = sc.nextLine();
            System.out.print("Data Fim: "); String fim = sc.nextLine();
            
            // Permite submissão mesmo que um esteja vazio (Controller lida com isso)
            System.out.println("\n" + c.obterDashboard(inicio, fim));
        }
        return false;
    }

    private boolean menuRH() {
        System.out.println("\n--- RECURSOS HUMANOS ---");
        System.out.println("1. Contratar Funcionário");
        System.out.println("2. Demitir Funcionário");
        System.out.println("0. Voltar");
        System.out.print(">>> ");
        
        String op = sc.nextLine();
        if (op.equals("1")) {
            System.out.print("Novo Username: "); String nu = sc.nextLine();
            System.out.print("Senha Inicial: "); String np = sc.nextLine();
            if (!nu.isBlank() && !np.isBlank()) {
                c.contratarFuncionario(nu, np, Funcao.FUNCIONARIO);
                System.out.println("Contratado.");
            }
        } else if (op.equals("2")) {
            List<String> funcs = c.getNomesFuncionarios();
            if (funcs.isEmpty()) { System.out.println("Nenhum funcionário elegível para demissão."); return false; }
            int idx = pick("Funcionário a demitir", funcs);
            if (idx != -1) {
                c.demitirFuncionario(idx);
                System.out.println("Funcionário removido.");
            }
        }
        return false;
    }

    private boolean menuStock() {
        List<String> ings = c.getIngredientes();
        int idx = pick("Selecione Ingrediente para Ajuste", ings);
        if (idx == -1) return false;

        System.out.print("Ajuste de quantidade (ex: 10 ou -5): ");
        try {
            int qtd = Integer.parseInt(sc.nextLine());
            c.atualizarStock(idx, qtd);
            System.out.println("Stock atualizado.");
        } catch (NumberFormatException e) { System.out.println("Valor inválido."); }
        return false;
    }

    private boolean menuEstacoes() {
        System.out.println("\n--- ESTAÇÕES DE TRABALHO ---");
        System.out.println("1. Adicionar Nova Estação");
        System.out.println("2. Remover Estação Existente");
        System.out.println("0. Voltar");
        System.out.print(">>> ");

        String op = sc.nextLine();
        if (op.equals("1")) {
            System.out.print("Nome da Estação (ex: Grelha 2): "); String nome = sc.nextLine();
            System.out.print("É um posto de Caixa? (s/n): "); String tipo = sc.nextLine();
            if (!nome.isBlank()) {
                c.adicionarEstacao(nome, tipo.equalsIgnoreCase("s"));
                System.out.println("Estação criada.");
            }
        } else if (op.equals("2")) {
            List<String> ests = c.getNomesEstacoes();
            if (ests.isEmpty()) { System.out.println("Sem estações para remover."); return false; }
            int idx = pick("Estação a remover", ests);
            if (idx != -1) {
                c.removerEstacao(idx);
                System.out.println("Estação removida.");
            }
        }
        return false;
    }

    private int pick(String t, List<String> l) {
        System.out.println("\n--- " + t + " ---");
        for(int i=0; i<l.size(); i++) System.out.println((i+1)+". "+l.get(i));
        System.out.println("0. Cancelar/Voltar");
        System.out.print("Opção > ");
        try { 
            int val = Integer.parseInt(sc.nextLine());
            if (val == 0) return -1;
            return (val > 0 && val <= l.size()) ? val - 1 : -1;
        } catch(Exception e) { return -1; }
    }
}