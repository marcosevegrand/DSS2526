package dss2526.ui.view;

import dss2526.ui.controller.VendaController;
import dss2526.ui.util.NewMenu;

import java.util.List;
import java.util.Scanner;

public class VendaUI {
    private final VendaController controller;
    private final Scanner scanner;

    public VendaUI(VendaController controller) {
        this.controller = controller;
        this.scanner = new Scanner(System.in);
    }

    public void show() {

        NewMenu menu = new NewMenu("--- Sistema de Venda ---", new String[]{
            "Iniciar Novo Pedido",
        });
        
        menu.setHandler(1, () -> { iniciarNovoPedido(); });

        menu.run();
    }

    private void iniciarNovoPedido() {
        System.out.println("ID Restaurante:");
        int id = scanner.nextInt();
        controller.novoPedido();
        System.out.println("Novo pedido iniciado.");
    }

    private Integer escolherRestaurante() {
        List<String> restaurantes = controller.listarRestaurantes();
        System.out.println("\n--- Escolher Restaurante ---");
        for (int i = 0; i < restaurantes.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, restaurantes.get(i));
        }
        int escolha = lerInt("Escolha um restaurante (0 para cancelar): ");
        if (escolha == 0) return null;
        return escolha - 1;
    }

    private Integer lerInt(String msg) {
        System.out.print(msg);
        return Integer.parseInt(scanner.nextLine());
    }

    private String lerString(String msg) {
        System.out.print(msg);
        return scanner.nextLine();
    }
}