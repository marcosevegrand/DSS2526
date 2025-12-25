package dss2526.ui.view;

import dss2526.gestao.IGestaoFacade; // IMPORTANTE: Resolve o erro de "cannot be resolved"
import dss2526.domain.enumeration.RoleTrabalhador;
import dss2526.domain.enumeration.Trabalho;
import dss2526.ui.delegate.NewMenu;

import java.util.List;
import java.util.Scanner;

public class GestaoUI {

    private final IGestaoFacade gestaoFacade;
    private final Scanner scanner;

    public GestaoUI(IGestaoFacade gestaoFacade) {
        this.gestaoFacade = gestaoFacade;
        this.scanner = new Scanner(System.in);
    }

    /**
     * Menu Principal de Gestão (Backoffice OCC)
     */
    public void run() { // Nomeado como 'run' para coincidir com a chamada na AppUI
        String[] opcoes = {
            "Registar Novo Restaurante",
            "Configurar Estação de Trabalho",
            "Contratar Funcionário",
            "Enviar Mensagem à Produção",
            "Consultar Alertas de Stock",
            "Atualizar Stock / Reabastecimento"
        };

        NewMenu menu = new NewMenu("Backoffice - Gestão Central (OCC)", opcoes);

        menu.setHandler(1, this::registarRestaurante);
        menu.setHandler(2, this::adicionarEstacao);
        menu.setHandler(3, this::contratarFuncionario);
        menu.setHandler(4, this::enviarMensagem);
        menu.setHandler(5, this::consultarAlertas);
        menu.setHandler(6, this::atualizarStock);

        menu.run();
    }

    // --- Ações de Gestão ---

    private void registarRestaurante() {
        System.out.println("\n>> Registar Restaurante");
        String nome = lerString("Nome: ");
        String local = lerString("Localização: ");
        
        try {
            int id = gestaoFacade.registarRestaurante(nome, local);
            System.out.println(">> Restaurante '" + nome + "' registado com ID: " + id);
        } catch (Exception e) {
            System.out.println(">> Erro ao registar: " + e.getMessage());
        }
    }

    private void adicionarEstacao() {
        System.out.println("\n>> Configurar Estação");
        int resId = lerInteiro("ID do Restaurante: ");
        
        System.out.println("Tipos: GRELHADOS, FRITOS, BEBIDAS, MONTAGEM, SOBREMESAS");
        String tipoStr = lerString("Tipo de Trabalho: ").toUpperCase();

        try {
            Trabalho tipo = Trabalho.valueOf(tipoStr);
            gestaoFacade.adicionarEstacao(resId, tipo);
            System.out.println(">> Estação de " + tipo + " adicionada com sucesso.");
        } catch (IllegalArgumentException e) {
            System.out.println(">> Erro: Tipo de trabalho inválido.");
        } catch (Exception e) {
            System.out.println(">> Erro: " + e.getMessage());
        }
    }

    private void contratarFuncionario() {
        System.out.println("\n>> Contratar Funcionário");
        String nome = lerString("Nome Completo: ");
        String user = lerString("Username: ");
        String pass = lerString("Password: ");
        
        System.out.println("Papéis: GERENTE, COZINHEIRO, CAIXA");
        String papelStr = lerString("Papel: ").toUpperCase();
        int resId = lerInteiro("ID do Restaurante: ");

        try {
            RoleTrabalhador papel = RoleTrabalhador.valueOf(papelStr);
            gestaoFacade.contratarFuncionario(nome, user, pass, papel, resId);
            System.out.println(">> Funcionário '" + user + "' registado no restaurante #" + resId);
        } catch (Exception e) {
            System.out.println(">> Erro: " + e.getMessage());
        }
    }

    private void enviarMensagem() {
        System.out.println("\n>> Enviar Mensagem à Produção");
        int resId = lerInteiro("ID do Restaurante (0 para todos): ");
        String texto = lerString("Mensagem: ");
        boolean urgente = lerString("Urgente? (S/N): ").equalsIgnoreCase("S");

        try {
            gestaoFacade.enviarMensagemProducao(resId, texto, urgente);
            System.out.println(">> Mensagem enviada.");
        } catch (Exception e) {
            System.out.println(">> Erro: " + e.getMessage());
        }
    }

    private void consultarAlertas() {
        System.out.println("\n>> Consulta de Alertas de Stock");
        int resId = lerInteiro("ID do Restaurante: ");
        
        try {
            List<String> alertas = gestaoFacade.getAlertasStock(resId);
            if (alertas.isEmpty()) {
                System.out.println("Nenhum alerta crítico de stock.");
            } else {
                alertas.forEach(a -> System.out.println(" [!] ALERTA: " + a));
            }
        } catch (Exception e) {
            System.out.println(">> Erro: " + e.getMessage());
        }
    }

    private void atualizarStock() {
        System.out.println("\n>> Reabastecimento de Stock");
        int resId = lerInteiro("ID do Restaurante: ");
        int ingId = lerInteiro("ID do Ingrediente: ");
        System.out.print("Quantidade a adicionar: ");
        float qtd = scanner.nextFloat();
        scanner.nextLine(); 

        try {
            gestaoFacade.atualizarStockLocal(ingId, resId, qtd);
            System.out.println(">> Stock atualizado.");
        } catch (Exception e) {
            System.out.println(">> Erro: " + e.getMessage());
        }
    }

    // --- Helpers ---

    private int lerInteiro(String msg) {
        System.out.print(msg);
        while (!scanner.hasNextInt()) {
            scanner.next(); 
            System.out.print("Inválido. Insira um número: ");
        }
        int n = scanner.nextInt();
        scanner.nextLine(); 
        return n;
    }

    private String lerString(String msg) {
        System.out.print(msg);
        return scanner.nextLine();
    }
}