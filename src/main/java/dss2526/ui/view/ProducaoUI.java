package dss2526.ui.view;

import dss2526.ui.controller.ProducaoController;
import dss2526.ui.util.NewMenu;
import java.util.*;

public class ProducaoUI {
    private final ProducaoController c = new ProducaoController();
    private final Scanner sc = new Scanner(System.in);

    public void run() {
        c.selecionarRestaurante(pick("RESTAURANTE", c.getRestaurantes()));
        c.selecionarEstacao(pick("POSTO DE TRABALHO", c.getEstacoes()));
        if(c.isCaixa()) runCaixa(); else runCozinha();
    }

    private void runCozinha() {
        NewMenu.builder("COZINHA - " + c.getNomeEstacao())
            .addOption("Tarefas Pendentes", () -> {
                List<String> ts = c.getTarefasDisponiveis();
                if(ts.isEmpty()) { System.out.println("Sem tarefas sincronizadas."); return false; }
                int i = pick("TAREFA", ts);
                System.out.println("1. Iniciar | 2. Concluir | 3. Atraso (Falta Stock)");
                String op = sc.nextLine();
                if(op.equals("1")) c.iniciarTarefa(i); 
                else if(op.equals("2")) c.concluirTarefa(i);
                else c.atrasarTarefa(i, pick("INGREDIENTE EM FALTA", c.getIngredientes()));
                return false;
            })
            .addOption("Monitor de Progresso", () -> { c.getMonitorGlobal().forEach(System.out::println); return false; })
            .run();
    }

    private void runCaixa() {
        NewMenu.builder("CAIXA - GESTÃO DE ENTREGAS E PAGAMENTOS")
            .addOption("Pagamentos Pendentes", () -> {
                List<String> up = c.getPedidosNaoPagos();
                if(!up.isEmpty()) c.confirmarPagamento(pick("SELECIONAR PEDIDO", up));
                return false;
            })
            .addOption("Entregar Pedidos Prontos", () -> {
                List<String> rd = c.getPedidosProntos();
                if(rd.isEmpty()) { System.out.println("Nenhum pedido pronto."); return false; }
                int pIdx = pick("ENTREGA", rd);
                System.out.print("1. Confirmar Entrega | 2. Reportar Erro (Refazer): ");
                if(sc.nextLine().equals("1")) c.entregarPedido(pIdx);
                else {
                    List<String> lines = c.getLinhasPedido(pIdx);
                    for(int i=0; i<lines.size(); i++) System.out.println((i+1)+". "+lines.get(i));
                    System.out.print("IDs das linhas a refazer (separados por espaço): ");
                    c.solicitarRefacao(pIdx, Arrays.stream(sc.nextLine().split(" ")).map(s->Integer.parseInt(s)-1).toList());
                }
                return false;
            })
            .addOption("Monitor de Progresso", () -> { c.getMonitorGlobal().forEach(System.out::println); return false; })
            .run();
    }

    private int pick(String t, List<String> l) {
        System.out.println("\n--- " + t + " ---");
        for(int i=0; i<l.size(); i++) System.out.println((i+1)+". "+l.get(i));
        System.out.print("Opção > "); 
        try { return Integer.parseInt(sc.nextLine())-1; } catch(Exception e) { return 0; }
    }
}