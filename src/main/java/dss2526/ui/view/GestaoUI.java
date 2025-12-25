package dss2526.ui.view;

import dss2526.domain.entity.Menu;
import dss2526.ui.controller.GestaoController;
import dss2526.ui.util.NewMenu;

import java.util.HashMap;
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
            "Gerir Catálogos"
        });
        
        menu.setHandler(1, () -> { gerirRestaurantes(); });
        menu.setHandler(2, () -> { gerirCatalogos(); });

        menu.run();
    }

    private void gerirRestaurantes() {
        NewMenu menu = new NewMenu("--- Gestão de Restaurantes ---", new String[]{
            "Adicionar Restaurante",
            "Listar Restaurantes",
            "Gerir um Restaurante",
        });

        menu.setHandler(1, () -> {
            System.out.println("\n--- Adicionar Restaurante ---");
            String nome = lerString("Nome: ");
            String localizacao = lerString("Localização: ");
            controller.adicionarRestaurante(nome, localizacao); });
        menu.setHandler(2, () -> {
            List<String> restaurantes = controller.listarRestaurantes();
            System.out.println("\n--- Lista de Restaurantes ---");
            restaurantes.forEach(System.out::println);
        });
        menu.setHandler(3, () -> { gerirUmRestaurante(); });

        menu.run();
    }

    private void gerirUmRestaurante() {
        Integer index = escolherRestaurante();
        if (index == null) return;

        NewMenu menu = new NewMenu("--- Gestão do Restaurante ---", new String[]{
            "Gerir Estações",
            "Gerir Funcionários",
            "Gerir Stock",
            "Definir Catálogo",
        });

        menu.setHandler(1, () -> { gerirEstacoes(index); });
        menu.setHandler(2, () -> { gerirFuncionarios(index); });
        menu.setHandler(3, () -> { gerirStock(index); });
        menu.setHandler(4, () -> { 
            Integer catalogoIndex = escolherCatalogo();
            if (catalogoIndex == null) return;
            controller.definirCatalogoRestaurante(index, catalogoIndex);
         });

        menu.run();
    }

    private void gerirEstacoes(Integer restauranteIndex) {
        NewMenu menu = new NewMenu("--- Gestão de Estações ---", new String[]{
            "Adicionar Estação",
            "Listar Estações",
        });

        menu.setHandler(1, () -> {
            System.out.println("\n--- Adicionar Estação ---");
            System.out.println("Tipo de Estação: ");
            List<String> tipos = controller.listarTiposEstacao();
            for (int i = 0; i < tipos.size(); i++) {
                System.out.printf("%d. %s%n", i + 1, tipos.get(i));
            }
            int escolha = lerInt("Escolha um tipo de estação (0 para cancelar): ");
            if (escolha == 0) return;
            String tipoEstacao = tipos.get(escolha - 1);
            controller.adicionarEstacao(restauranteIndex, tipoEstacao);
        });

        menu.setHandler(2, () -> {
            List<String> estacoes = controller.listarEstacoes(restauranteIndex);
            System.out.println("\n--- Lista de Estações ---");
            estacoes.forEach(System.out::println);
        });
    }

    private void gerirFuncionarios(Integer restauranteIndex) {
        NewMenu menu = new NewMenu("--- Gestão de Funcionários ---", new String[]{
            "Adicionar Funcionário",
            "Listar Funcionários",
        });

        menu.setHandler(1, () -> {
            System.out.println("\n--- Adicionar Funcionário ---");
            String utilizador = lerString("Utilizador: ");
            String pass = lerString("Password: ");
            System.out.println("Função: ");
            List<String> funcoes = controller.listarFuncoes();
            for (int i = 0; i < funcoes.size(); i++) {
                System.out.printf("%d. %s%n", i + 1, funcoes.get(i));
            }
            Integer funcaoIndex = lerInt("Escolha uma função (0 para cancelar): ");
            if (funcaoIndex == 0) return;
            controller.adicionarFuncionario(restauranteIndex, utilizador, pass, funcaoIndex);
        });
    }

    private void gerirStock(Integer restauranteIndex) {
        NewMenu menu = new NewMenu("--- Gestão de Stock ---", new String[]{
            "Adicionar Ingrediente ao Stock",
            "Aumentar Quantidade de Ingrediente no Stock",
            "Diminuir Quantidade de Ingrediente no Stock",
            "Listar Ingredientes do Stock",
        });

        menu.setHandler(1, () -> {
            System.out.println("\n--- Adicionar Ingrediente ao Stock ---");
            String nome = lerString("Nome: ");
            int quantidade = lerInt("Quantidade: ");
            String unidade = lerString("Unidade de Medida: ");
            String alergenico = lerString("Alergénico: ");
            controller.adicionarIngredienteStock(restauranteIndex, nome, unidade, quantidade, alergenico);
        });
        menu.setHandler(2, () -> {
            System.out.println("\n--- Aumentar Quantidade de Ingrediente no Stock ---");
            Integer ingredienteIndex = escolherIngrediente(restauranteIndex);
            if (ingredienteIndex == null) return;
            int quantidade = lerInt("Quantidade a Adicionar: ");
            controller.aumentarIngredienteStock(restauranteIndex, ingredienteIndex, quantidade);
        });
        menu.setHandler(3, () -> {
            System.out.println("\n--- Diminuir Quantidade de Item no Stock ---");
            Integer ingredienteIndex = escolherIngrediente(restauranteIndex);
            if (ingredienteIndex == null) return;
            int quantidade = lerInt("Quantidade a Diminuir: ");
            controller.diminuirIngredienteStock(restauranteIndex, ingredienteIndex, quantidade);
        });
        menu.setHandler(4, () -> {
            List<String> stockItems = controller.listarIngredientes(restauranteIndex);
            System.out.println("\n--- Lista de Ingredientes do Stock ---");
            stockItems.forEach(System.out::println);
        });
    }
    
    private void gerirCatalogos() {
        NewMenu menu = new NewMenu("--- Gestão de Catálogos ---", new String[]{
                "Adicionar Catálogo",
                "Remover Catálogo",
                "Listar Catálogos",
                "Gerir Items de um Catálogo"
        });

        menu.run();
    }

    private Integer escolherIngrediente(Integer restauranteIndex) {
        List<String> ingredientes = controller.listarIngredientes(restauranteIndex);
        System.out.println("\n--- Escolher Ingrediente ---");
        for (int i = 0; i < ingredientes.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, ingredientes.get(i));
        }
        int escolha = lerInt("Escolha um ingrediente (0 para cancelar): ");
        if (escolha == 0) return null;
        return escolha - 1;
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

    private Integer escolherCatalogo() {
        List<String> catalogos = controller.listarCatalogos();
        System.out.println("\n--- Escolher Catálogo ---");
        for (int i = 0; i < catalogos.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, catalogos.get(i));
        }
        int escolha = lerInt("Escolha um catálogo (0 para cancelar): ");
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