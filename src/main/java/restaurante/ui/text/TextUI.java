package restaurante.ui.text;

import restaurante.business.IRestauranteFacade;
import java.util.Scanner;

/**
 * Text-based user interface for the restaurant system
 * This is a placeholder - you should implement a graphical UI with touchscreens
 */
public class TextUI {
    
    private IRestauranteFacade facade;
    private Scanner scanner;
    
    public TextUI(IRestauranteFacade facade) {
        this.facade = facade;
        this.scanner = new Scanner(System.in);
    }
    
    public void iniciar() {
        System.out.println("Sistema de Gestão de Restaurantes");
        System.out.println("==================================");
        
        while (true) {
            exibirMenuPrincipal();
            int opcao = lerOpcao();
            
            switch (opcao) {
                case 1:
                    menuCliente();
                    break;
                case 2:
                    menuFuncionario();
                    break;
                case 3:
                    menuGerente();
                    break;
                case 0:
                    System.out.println("A sair...");
                    return;
                default:
                    System.out.println("Opção inválida!");
            }
        }
    }
    
    private void exibirMenuPrincipal() {
        System.out.println("\n=== MENU PRINCIPAL ===");
        System.out.println("1. Cliente (Fazer Pedido)");
        System.out.println("2. Funcionário (Preparar Pedido)");
        System.out.println("3. Gerente (Consultar Estatísticas)");
        System.out.println("0. Sair");
        System.out.print("Escolha uma opção: ");
    }
    
    private void menuCliente() {
        System.out.println("\n=== TERMINAL DE VENDA (CLIENTE) ===");
        System.out.println("TODO: Implementar interface de cliente");
        // TODO: Implement client interface for making orders
    }
    
    private void menuFuncionario() {
        System.out.println("\n=== TERMINAL DE PRODUÇÃO (FUNCIONÁRIO) ===");
        System.out.print("Código: ");
        String codigo = scanner.nextLine();
        System.out.print("Palavra-passe: ");
        String senha = scanner.nextLine();
        
        if (facade.autenticarFuncionario(codigo, senha)) {
            System.out.println("Autenticado com sucesso!");
            // TODO: Show employee interface
        } else {
            System.out.println("Credenciais inválidas!");
        }
    }
    
    private void menuGerente() {
        System.out.println("\n=== PAINEL DO GERENTE ===");
        System.out.print("Código: ");
        String codigo = scanner.nextLine();
        System.out.print("Palavra-passe: ");
        String senha = scanner.nextLine();
        
        if (facade.autenticarGerente(codigo, senha)) {
            System.out.println("Autenticado com sucesso!");
            // TODO: Show manager interface with statistics
        } else {
            System.out.println("Credenciais inválidas!");
        }
    }
    
    private int lerOpcao() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
