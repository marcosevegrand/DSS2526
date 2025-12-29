package dss2526.ui.view;

import dss2526.ui.controller.ProducaoController;
import dss2526.ui.util.NewMenu;
import java.util.List;
import java.util.Scanner;

public class ProducaoUI {
    private final ProducaoController controller;
    private final Scanner sc;

    public ProducaoUI() {
        this.controller = new ProducaoController();
        this.sc = new Scanner(System.in);
    }

    public void run() {
        System.out.println("SISTEMA DE PRODUCAO");
        List<String> rests = controller.listarNomesRestaurantes();
        if (rests.isEmpty()) return;
        controller.selecionarRestaurante(escolher("Selecione o Restaurante", rests));

        List<String> ests = controller.listarNomesEstacoes();
        if (ests.isEmpty()) return;
        controller.selecionarEstacao(escolher("Selecione Posto", ests));

        if (controller.ehEstacaoDeCaixa()) menuCaixa();
        else menuCozinha();
    }

    private void menuCozinha() {
        NewMenu menu = new NewMenu("COZINHA", new String[]{
            "Iniciar Nova Tarefa",
            "Concluir Tarefa Ativa",
            "Monitor Global"
        });
        menu.setHandler(1, () -> {
            List<String> novas = controller.getTarefasNovas();
            if (novas.isEmpty()) return false;
            controller.iniciarTarefaSelecionada(escolher("Selecionar para INICIAR (Stock será ajustado agora)", novas));
            return false;
        });
        menu.setHandler(2, () -> {
            List<String> curso = controller.getTarefasEmCurso();
            if (curso.isEmpty()) return false;
            controller.concluirTarefaSelecionada(escolher("Concluir", curso));
            return false;
        });
        menu.setHandler(3, () -> { controller.getMonitorGlobal().forEach(System.out::println); return false; });
        menu.run();
    }

    private void menuCaixa() {
        NewMenu menu = new NewMenu("CAIXA", new String[]{
            "Entregar Pedidos Prontos / Reportar Falha",
            "Monitor Global"
        });
        menu.setHandler(1, () -> {
            List<String> prontos = controller.getPedidosProntos();
            if (prontos.isEmpty()) { System.out.println("Sem pedidos prontos."); return false; }
            int pIdx = escolher("Pedidos Prontos", prontos);
            
            System.out.println("1. Confirmar Entrega | 2. Inspecionar Itens/Refazer");
            String op = sc.nextLine();
            if (op.equals("1")) {
                controller.confirmarEntrega(pIdx);
                System.out.println("Pedido entregue.");
            } else {
                List<String> linhas = controller.getLinhasDePedido(pIdx);
                int lIdx = escolher("Selecione o item com falha para REFAZER", linhas);
                controller.refazerLinha(lIdx);
                System.out.println("Pedido de re-confeção enviado para a cozinha.");
            }
            return false;
        });
        menu.run();
    }

    private int escolher(String t, List<String> ops) {
        System.out.println("\n" + t);
        for (int i = 0; i < ops.size(); i++) System.out.println((i + 1) + ". " + ops.get(i));
        System.out.print("Seleccao: ");
        try { return Integer.parseInt(sc.nextLine()) - 1; } catch (Exception e) { return 0; }
    }
}