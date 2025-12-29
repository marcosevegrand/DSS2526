package dss2526.ui.view;

import dss2526.ui.controller.ProducaoController;
import dss2526.ui.util.NewMenu;
import java.util.*;

public class ProducaoUI {
    private final ProducaoController c = new ProducaoController();
    private final Scanner sc = new Scanner(System.in);

    public void run() {
        List<String> rests = c.getRestaurantes();
        if (rests.isEmpty()) { System.out.println("Sem restaurantes."); return; }
        
        int rIdx = pick("SELECIONAR RESTAURANTE", rests);
        if (rIdx == -1) return; 
        c.selecionarRestaurante(rIdx);

        List<String> ests = c.getEstacoes();
        if (ests.isEmpty()) { System.out.println("Sem estações."); return; }
        
        int eIdx = pick("POSTO DE TRABALHO", ests);
        if (eIdx == -1) return;
        c.selecionarEstacao(eIdx);

        if(c.isCaixa()) runCaixa(); else runCozinha();
    }

    private void runCozinha() {
        NewMenu.builder("COZINHA - " + c.getNomeEstacao())
            .addOption("Iniciar Tarefas (Pendentes)", () -> {
                List<String> ts = c.getTarefasPendentes();
                if(ts.isEmpty()) { System.out.println("Nenhuma tarefa pendente para iniciar."); return false; }
                
                int tIdx = pick("SELECIONE A TAREFA PARA INICIAR", ts);
                if (tIdx == -1) return false;

                c.iniciarTarefaPendente(tIdx);
                System.out.println("Tarefa iniciada! Ela agora está disponível em 'Tarefas em Curso'.");
                return false;
            })
            .addOption("Gerir Tarefas em Curso (Concluir/Atrasar)", () -> {
                List<String> ts = c.getTarefasEmExecucao();
                if(ts.isEmpty()) { System.out.println("Nenhuma tarefa em execução nesta estação."); return false; }
                
                int tIdx = pick("SELECIONE A TAREFA EM CURSO", ts);
                if (tIdx == -1) return false;

                System.out.println("\n[Ação na Tarefa]");
                System.out.println("1. Concluir");
                System.out.println("2. Atrasar (Falta de Stock)");
                System.out.println("0. Voltar");
                System.out.print(">>> ");
                String op = sc.nextLine();
                
                if(op.equals("1")) {
                    c.concluirTarefaEmExecucao(tIdx);
                    System.out.println("Tarefa concluída!");
                } else if(op.equals("2")) {
                    List<String> ings = c.getIngredientesDaTarefaEmExecucao(tIdx);
                    if (ings.isEmpty()) {
                        System.out.println("Erro: Tarefa sem ingredientes registados.");
                    } else {
                        int ingIdx = pick("QUAL INGREDIENTE ESTÁ EM FALTA?", ings);
                        if (ingIdx != -1) {
                            c.atrasarTarefaEmExecucao(tIdx, ingIdx);
                            System.out.println("Atraso registado e alerta enviado!");
                        }
                    }
                }
                return false;
            })
            .addOption("Ver Mensagens", () -> {
                System.out.println("\n--- MENSAGENS ---");
                List<String> msgs = c.getMensagens();
                if(msgs.isEmpty()) System.out.println("(Nenhuma mensagem)");
                else msgs.forEach(System.out::println);
                return false;
            })
            .addOption("Monitor Global de Pedidos", () -> { 
                System.out.println("\n--- MONITOR ---");
                c.getMonitorGlobal().forEach(System.out::println); 
                return false; 
            })
            .run();
    }

    private void runCaixa() {
        NewMenu.builder("CAIXA - GESTÃO DE PAGAMENTOS E ENTREGAS")
            .addOption("Processar Pagamentos Pendentes", () -> {
                List<String> up = c.getPedidosNaoPagos();
                if(up.isEmpty()) { System.out.println("Tudo pago."); return false; }
                
                int idx = pick("CONFIRMAR PAGAMENTO", up);
                if (idx == -1) return false;

                String msg = c.confirmarPagamento(idx);
                System.out.println("\n" + msg);
                return false;
            })
            .addOption("Entregar Pedidos Prontos", () -> {
                List<String> rd = c.getPedidosProntos();
                if(rd.isEmpty()) { System.out.println("Nenhum pedido pronto."); return false; }
                
                int pIdx = pick("SELECIONAR PEDIDO PARA ENTREGA", rd);
                if (pIdx == -1) return false;

                System.out.println("1. Confirmar Entrega (Tudo OK)");
                System.out.println("2. Reportar Erro (Devolver para Refazer)");
                System.out.println("0. Cancelar");
                System.out.print(">>> ");
                String op = sc.nextLine();

                if(op.equals("1")) {
                    c.entregarPedido(pIdx);
                    System.out.println("Pedido entregue com sucesso.");
                } else if (op.equals("2")) {
                    List<String> lines = c.getLinhasPedido(pIdx);
                    System.out.println("\n--- ITENS DO PEDIDO ---");
                    for(int i=0; i<lines.size(); i++) System.out.println((i+1)+". "+lines.get(i));
                    
                    System.out.print("Indique os números das linhas a refazer (ex: 1 3): ");
                    try {
                        String input = sc.nextLine();
                        if (!input.isBlank()) {
                            List<Integer> indices = Arrays.stream(input.split(" "))
                                .map(s -> Integer.parseInt(s.trim()) - 1)
                                .toList();
                            c.solicitarRefacao(pIdx, indices);
                            System.out.println("Pedido devolvido à cozinha.");
                        }
                    } catch (Exception e) { System.out.println("Entrada inválida."); }
                }
                return false;
            })
            .addOption("Ver Mensagens", () -> {
                System.out.println("\n--- MENSAGENS ---");
                List<String> msgs = c.getMensagens();
                if(msgs.isEmpty()) System.out.println("(Nenhuma mensagem)");
                else msgs.forEach(System.out::println);
                return false;
            })
            .addOption("Monitor Global", () -> { 
                System.out.println("\n--- MONITOR ---");
                c.getMonitorGlobal().forEach(System.out::println); 
                return false; 
            })
            .run();
    }

    private int pick(String t, List<String> l) {
        System.out.println("\n--- " + t + " ---");
        for(int i=0; i<l.size(); i++) System.out.println((i+1)+". "+l.get(i));
        System.out.println("0. Sair");
        System.out.print("Opção > "); 
        try { 
            int val = Integer.parseInt(sc.nextLine());
            if (val == 0) return -1;
            int res = val - 1;
            return (res >= 0 && res < l.size()) ? res : -1;
        } catch(Exception e) { return -1; }
    }
}