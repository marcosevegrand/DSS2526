package dss2526.ui.view;

import dss2526.ui.controller.VendaController;
import java.util.Scanner;

public class VendaUI {
    private final VendaController controller;
    private final Scanner scanner;

    public VendaUI(VendaController controller) {
        this.controller = controller;
        this.scanner = new Scanner(System.in);
    }

    public void show() {
        System.out.println("\n--- Menu Venda ---");
        System.out.println("1. Novo Pedido");
        System.out.println("2. Finalizar Pedido Atual");
        System.out.println("0. Voltar");

        int opcao = scanner.nextInt();
        scanner.nextLine();

        switch (opcao) {
            case 1:
                novoPedido();
                break;
            case 2:
                controller.finalizarPedido();
                System.out.println("Pedido finalizado.");
                break;
            case 0:
                return;
            default:
                System.out.println("Opção inválida.");
        }
    }

    private void novoPedido() {
        System.out.println("ID Restaurante:");
        int id = scanner.nextInt();
        controller.novoPedido(id);
        System.out.println("Novo pedido iniciado.");
    }
}