package dss2526.ui.view;

import java.util.*;

import dss2526.ui.controller.ProducaoController;
import dss2526.ui.delegate.NewMenu;

public class ProducaoUI {

    private ProducaoController producao;
    private Scanner sc;

    public ProducaoUI(ProducaoController producao) {
        this.producao = producao;
        this.sc = new Scanner(System.in);
    }

    public void run() {

        // Fazer escolher restaurante e estação de trabalho antes de mostrar o menu

        NewMenu menu = new NewMenu(
            "Subsistema de Produção",
            new String[] {
            "Listar Tarefas Pendentes",
            "Atualizar Estado de Tarefa"
        });
        menu.setHandler(1, () -> exemplo());

        menu.run();
    }

    private void exemplo() {
        System.out.println("\nExemplo de funcionalidade do subsistema de Produção.");
    }

    private Integer lerInt(String msg) {
        System.out.print(msg);
        return Integer.parseInt(sc.nextLine());
    }

    private String lerString(String msg) {
        System.out.print(msg);
        return sc.nextLine();
    }
}