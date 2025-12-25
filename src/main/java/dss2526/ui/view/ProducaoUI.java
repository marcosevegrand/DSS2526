package dss2526.ui.view;

import dss2526.domain.entity.Pedido;
import dss2526.ui.controller.ProducaoController;

import java.util.List;
import java.util.Scanner;

public class ProducaoUI {
    private final ProducaoController controller;
    private final Scanner scanner;

    public ProducaoUI(ProducaoController controller) {
        this.controller = controller;
        this.scanner = new Scanner(System.in);
    }

    public void show() {
        System.out.println("\n--- Menu Produção ---");
        System.out.println("1. Ver Fila de Pedidos");
        System.out.println("2. Concluir Tarefa"); // Simplificado
        System.out.println("0. Voltar");

        int opcao = scanner.nextInt();
        scanner.nextLine();

        switch (opcao) {
            case 1:
                verFila();
                break;
            case 2:
                concluirTarefa();
                break;
            case 0:
                return;
            default:
                System.out.println("Opção inválida.");
        }
    }

    private void verFila() {
        System.out.println("ID do Restaurante:"); // Em app real seria contexto do user
        int restId = scanner.nextInt();
        List<Pedido> pedidos = controller.getFilaPedidos(restId);
        for(Pedido p : pedidos) {
            System.out.println("Pedido #" + p.getId() + " - " + p.getEstado());
        }
    }

    private void concluirTarefa() {
        System.out.println("ID Tarefa:");
        int id = scanner.nextInt();
        controller.concluirTarefa(id);
        System.out.println("Tarefa concluída.");
    }
}