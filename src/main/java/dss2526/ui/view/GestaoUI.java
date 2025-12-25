package dss2526.ui.view;

import dss2526.domain.enumeration.RoleTrabalhador;
import dss2526.domain.enumeration.Trabalho;
import dss2526.gestao.IGestaoFacade;
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
    public void show() {
        String[] opcoes = {
            "Registar Novo Restaurante",
            "Configurar Estações de Trabalho",
            "Contratar Funcionário",
            "Enviar Mensagem à Produção",
            "Consultar Alertas de Stock",
            "Atualizar Stock / Reabastecimento"
        };

        NewMenu menu = new NewMenu("Backoffice - Gestão Central (OCC)", opcoes);

        menu.setHandler(1, () -> registarRestaurante());
        menu.setHandler(2, () -> adicionarEstacao());
        menu.setHandler(3, () -> contratarFuncionario());
        menu.setHandler(4, () -> enviarMensagem());
        menu.setHandler(5, () -> consultarAlertas());
        menu.setHandler(6, () -> atualizarStock());

        menu.run();
    }

    // --- Ações de Gestão ---

    private void registarRestaurante() {
        System.out.println("\n>> Registar Restaurante");
        String nome = lerString("Nome: ");
        String local = lerString("Localização: ");
        
        try {
            gestaoFacade.registarRestaurante(nome, local);
            System.out.println(">> Restaurante '" + nome + "' registado com sucesso!");
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
            System.out.println(">> Estação de " + tipo + " adicionada ao restaurante #" + resId);
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
        int resId = lerInteiro("ID do Restaurante Alvo: ");
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
            System.out.println("\n--- Alertas Pendentes ---");
            if (alertas.isEmpty()) {
                System.out.println("Nenhum alerta de rutura de stock.");
            } else {
                alertas.forEach(alerta -> System.out.println(" [!] " + alerta));
            }
        } catch (Exception e) {
            System.out.println(">> Erro ao consultar: " + e.getMessage());
        }
    }

    private void atualizarStock() {
        System.out.println("\n>> Reabastecimento de Stock");
        int resId = lerInteiro("ID do Restaurante: ");
        int ingId = lerInteiro("ID do Ingrediente: ");
        System.out.print("Quantidade a adicionar: ");
        float qtd = scanner.nextFloat();
        scanner.nextLine(); // limpar buffer

        try {
            gestaoFacade.atualizarStockLocal(ingId, resId, qtd);
            System.out.println(">> Stock atualizado. Os pedidos bloqueados serão reavaliados.");
        } catch (Exception e) {
            System.out.println(">> Erro ao atualizar: " + e.getMessage());
        }
    }

    // --- Helpers de Input (Estilo VendaUI) ---

    private int lerInteiro(String msg) {
        System.out.print(msg);
        while (!scanner.hasNextInt()) {
            scanner.next(); 
            System.out.print("Número inválido. Tente novamente: ");
        }
        int num = scanner.nextInt();
        scanner.nextLine(); 
        return num;
    }

    private String lerString(String msg) {
        System.out.print(msg);
        return scanner.nextLine();
    }
}