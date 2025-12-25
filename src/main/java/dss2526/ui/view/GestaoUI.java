package dss2526.ui.view;

import java.util.*;

import dss2526.ui.controller.GestaoController;
import dss2526.ui.delegate.NewMenu;

public class GestaoUI {

    private GestaoController gestao;
    private Scanner sc;

    public GestaoUI(GestaoController gestao) {
        this.gestao = gestao;
        this.sc = new Scanner(System.in);
    }

    public void run() {
        NewMenu menu = new NewMenu(
            "Subsistema de Gestão",
            new String[] {
            "Gerir Catálogos", // adicionar/remover/editar catálogos, menus, produtos, ingredientes
            "Gerir Restaurantes", // adicionar/remover/editar restaurantes, estações, funcionarios
            // ^ enviar uma mensagem aos terminais de producao de trabalho
            "Estatísticas" // consultar estatísticas de vendas, produção, desempenho de um restaurante ou todos
        });
        menu.setHandler(1, () -> exemplo());

        menu.run();
    }

    private void exemplo() {
        System.out.println("\nExemplo de funcionalidade do subsistema de Gestão.");
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