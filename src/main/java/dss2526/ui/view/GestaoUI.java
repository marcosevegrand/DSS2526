package dss2526.ui.view;

import dss2526.ui.controller.GestaoController;
import dss2526.ui.util.NewMenu;

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

        NewMenu menu = new NewMenu("--- Sistema de Gestão ---", new String[]{
            "Gerir Restaurantes",
            "Ver Estatísticas",
        });
        
        menu.setHandler(1, () -> { gerirRestaurantes(); });
        // ...

        menu.run();
    }

    private void gerirRestaurantes() {
        NewMenu menu = new NewMenu("--- Gestão de Restaurantes ---", new String[]{
            "Registar Restaurante",
            "Remover Restaurante",
            "Listar Restaurantes",
        });

        menu.setHandler(1, () -> {
            System.out.println("\n--- Registar Restaurante ---");
            String nome = lerString("Nome: ");
            String localizacao = lerString("Localização: ");
            controller.registarRestaurante(nome, localizacao);
        });

        menu.setHandler(2, () -> {
            List<String> restaurantes = controller.listarRestaurantes();
            System.out.println("\n--- Lista de Restaurantes ---");
            restaurantes.forEach(System.out::println);
        });

        menu.setHandler(3, () -> {
            System.out.println("\n--- Remover Restaurante ---");
            Integer i = escolher("Restaurante", controller.listarRestaurantes());
            if (i != null) {
                controller.removerRestaurante(i);
            }
        });

        menu.run();
    }

    private Integer escolher(String titulo, List<String> opcoes) {
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