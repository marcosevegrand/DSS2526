package restaurante.ui.text;

import restaurante.business.IRestauranteFacade;
import java.util.Scanner;

public class TextUI {

    private final IRestauranteFacade facade;
    private final Scanner scanner;

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
        // aqui chamas métodos da facade para criar pedido, adicionar itens, pagar, etc.
        // ex:
        // int idPedido = facade.iniciarPedidoCliente();
        // ciclo de adicionar itens e no fim facade.confirmarPedido(idPedido);
    }

    private void menuFuncionario() {
        System.out.println("\n=== TERMINAL DE PRODUÇÃO (FUNCIONÁRIO) ===");
        System.out.print("Código: ");
        String codigo = scanner.nextLine();
        System.out.print("Palavra-passe: ");
        String senha = scanner.nextLine();

        if (facade.autenticarFuncionario(codigo, senha)) {
            System.out.println("Autenticado com sucesso!");
            // aqui podes ter outro submenu para ver fila, iniciar/concluir pedidos, etc.
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
            // submenu de estatísticas e mensagens para terminais, via facade
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
