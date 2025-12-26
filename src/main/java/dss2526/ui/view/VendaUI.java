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
        Integer restauranteIndex = escolher("Restaurante", controller.listarRestaurantes());

        NewMenu menu = new NewMenu("--- Sistema de Venda ---", new String[]{
            "Iniciar Novo Pedido",
        });
        
        menu.setHandler(1, () -> { iniciarNovoPedido(restauranteIndex); });

        menu.run();
    }

    private void iniciarNovoPedido(Integer restauranteIndex) {
        String input = lerString("Pedido para levar? (s/n) : ").trim().toLowerCase();
        boolean paraLevar = input.equals("s") || input.equals("sim");

        controller.iniciarPedido(restauranteIndex, paraLevar);

        NewMenu menu = new NewMenu("--- Pedido ---", new String[]{
            "Adicionar Item",
            "Remover Item",
            "Consultar Pedido",
            "Finalizar Pedido",
            "Cancelar Pedido",
        });

        menu.setHandler(1, () -> {
            List<String> itens = controller.listarItensDisponiveis(restauranteIndex);
            Integer itemIndex = escolher("Item", itens);
            if (itemIndex != null) {
                Integer quantidade = lerInt("Quantidade: ");
                String nota = lerString("Nota (opcional): ");
                controller.adicionarItemAoPedido(itemIndex, quantidade);
            }
        });

        menu.setHandler(2, () -> {
            List<String> itensPedido = controller.listarItensDoPedido();
            Integer itemIndex = escolher("Item a remover", itensPedido);
            if (itemIndex != null) {
                controller.removerItemDoPedido(itemIndex);
            }
        });

        menu.setHandler(3, () -> { 
            List<String> detalhes = controller.consultarPedido();
            detalhes.forEach(System.out::println);
        });

        menu.setHandler(4, () -> {
            controller.finalizarPedido();
            System.out.println("Pedido finalizado com sucesso!");
            return true;
        });

    }

    private Integer escolher(String titulo, List<String> opcoes) {
        System.out.printf("\n--- Escolher %s ---", titulo);
        for (int i = 0; i < opcoes.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, opcoes.get(i));
        }
        int escolha = lerInt(String.format("Escolha um %s (0 para cancelar): ", titulo));
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