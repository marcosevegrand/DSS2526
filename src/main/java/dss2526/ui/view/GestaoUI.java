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
            "Gerir Estações",
            "Gerir Funcionários",
            "Gerir Stock",
            "Gerir Catálogos",
            "Gerir Menus",
            "Gerir Produtos",
            "Gerir Ingredientes",
            "Ver Estatísticas",
        });
        
        menu.setHandler(1, () -> { gerirRestaurantes(); });
        // ...

        menu.run();
    }

    private void gerirRestaurantes() {
        NewMenu menu = new NewMenu("--- Gestão de Restaurantes ---", new String[]{
            "Registar Restaurante",
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

        menu.run();
    }

    // ... Other gerirX methods would go here ...

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