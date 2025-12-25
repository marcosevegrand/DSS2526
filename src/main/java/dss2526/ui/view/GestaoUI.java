package dss2526.ui.view;

import dss2526.domain.entity.Menu;
import dss2526.ui.controller.GestaoController;

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
        System.out.println("\n--- Menu Gestão ---");
        System.out.println("1. Registar Funcionário");
        System.out.println("2. Listar Menus");
        System.out.println("0. Voltar");
        
        int opcao = scanner.nextInt();
        scanner.nextLine(); // consume newline
        
        switch (opcao) {
            case 1:
                registarFuncionario();
                break;
            case 2:
                listarMenus();
                break;
            case 0:
                return;
            default:
                System.out.println("Opção inválida.");
        }
    }

    private void registarFuncionario() {
        System.out.println("Nome:");
        String nome = scanner.nextLine();
        System.out.println("User:");
        String user = scanner.nextLine();
        System.out.println("Pass:");
        String pass = scanner.nextLine();
        System.out.println("Função (GERENTE, FUNCIONARIO...):");
        String funcao = scanner.nextLine();

        controller.adicionarFuncionario(nome, user, pass, funcao);
        System.out.println("Funcionário registado.");
    }

    private void listarMenus() {
        List<Menu> menus = controller.getMenus();
        for (Menu m : menus) {
            System.out.println("ID: " + m.getId() + " | " + m.getNome() + " | " + m.getPreco() + "€");
        }
    }
}