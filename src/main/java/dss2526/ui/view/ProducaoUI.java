package dss2526.ui.view;

import dss2526.ui.controller.ProducaoController;
import dss2526.ui.util.NewMenu;

import java.util.List;
import java.util.Scanner;

public class ProducaoUI {
    
    private final ProducaoController controller;
    private final Scanner scanner;

    public ProducaoUI() {
        this.controller = new ProducaoController();
        this.scanner = new Scanner(System.in);
    }

    public void run() {
        mostrarCabecalho();

        // 1. Setup Contexto
        List<String> rests = controller.getListaRestaurantes();
        if (rests.isEmpty()) { 
            System.out.println("❌ Nenhum restaurante encontrado."); 
            return; 
        }
        
        System.out.println("📍 LOGIN DE FUNCIONÁRIO");
        Integer rIdx = escolher("🏢 Selecione o Restaurante", rests);
        if (rIdx == null) return;
        controller.selecionarRestaurante(rIdx);

        List<String> ests = controller.getListaEstacoes();
        if (ests.isEmpty()) {
            System.out.println("❌ Este restaurante não tem estações configuradas.");
            return;
        }

        Integer eIdx = escolher("🔧 Selecione a sua Estação de Trabalho", ests);
        if (eIdx == null) return;
        controller.selecionarEstacao(eIdx);

        // 2. Menu Loop (Opção de Alerta Geral removida)
        NewMenu menu = new NewMenu("TERMINAL DE PRODUÇÃO", new String[]{
            "📥 Visualizar Novas Tarefas (Iniciar)",
            "⚙️  Minhas Tarefas em Curso (Concluir/Atraso)",
            "📩 Ver Mensagens da Gestão",
            "🔎 Consultar Estado Global dos Pedidos"
        });

        menu.setHandler(1, () -> { fluxoTarefasPendentes(); return false; });
        menu.setHandler(2, () -> { fluxoTarefasEmCurso(); return false; });
        menu.setHandler(3, () -> { fluxoMensagens(); return false; });
        menu.setHandler(4, () -> { fluxoEstadoGlobal(); return false; });

        menu.run();
    }

    private void fluxoTarefasPendentes() {
        separador(); System.out.println("📥 TAREFAS DISPONÍVEIS PARA INICIAR"); separador();
        List<String> tarefas = controller.getTarefasPendentesFormatadas();
        if (tarefas.isEmpty()) { System.out.println("✅ Nenhuma tarefa disponível."); esperarEnter(); return; }
        Integer idx = escolher("Selecione para INICIAR", tarefas);
        if (idx != null) { controller.iniciarTarefaPendente(idx); System.out.println("🚀 Tarefa iniciada!"); }
    }

    private void fluxoTarefasEmCurso() {
        separador(); System.out.println("⚙️  SUAS TAREFAS EM EXECUÇÃO"); separador();
        List<String> emCurso = controller.getTarefasEmCursoFormatadas();
        if (emCurso.isEmpty()) { System.out.println("ℹ️  Não tem tarefas em execução."); esperarEnter(); return; }
        Integer idxTarefa = escolher("Selecione a Tarefa", emCurso);
        if (idxTarefa == null) return;
        
        NewMenu menuAcao = new NewMenu("AÇÃO SOBRE TAREFA", new String[]{ "✅ Marcar como Concluída", "⚠️  Marcar como Atrasada" });
        menuAcao.setHandler(1, () -> { 
            controller.concluirTarefaEmCurso(idxTarefa); 
            System.out.println("✨ Tarefa concluída!"); 
            return true; 
        });
        menuAcao.setHandler(2, () -> {
            List<String> ings = controller.getIngredientesDaTarefaParaSelecao(idxTarefa);
            if (ings.isEmpty()) { 
                System.out.println("❌ Sem ingredientes específicos associados à tarefa."); 
                return true; 
            }
            Integer idxIng = escolher("Qual ingrediente está em falta?", ings);
            if (idxIng != null) { 
                controller.reportarAtrasoTarefaPorIndexIngrediente(idxTarefa, idxIng); 
                System.out.println("🚨 Atraso registado!"); 
            }
            return true; 
        });
        menuAcao.run();
    }

    private void fluxoMensagens() {
        separador(); System.out.println("📩 MENSAGENS RECEBIDAS (Últimas 24h)");
        List<String> msgs = controller.getNovasMensagens();
        if (msgs.isEmpty()) System.out.println("ℹ️  Sem novas mensagens."); else msgs.forEach(System.out::println);
        separador(); esperarEnter();
    }

    private void fluxoEstadoGlobal() {
        while (true) {
            separador(); System.out.println("🔎 ESTADO GLOBAL DOS PEDIDOS"); separador();
            List<String> pedidos = controller.getPedidosGlobaisFormatados();
            if (pedidos.isEmpty()) { System.out.println("✅ Sem pedidos em produção."); esperarEnter(); return; }
            
            Integer idxPedido = escolher("Selecione um Pedido para ver detalhes", pedidos);
            if (idxPedido == null) return;
            
            // Cabeçalho claro do pedido
            String tituloPedido = pedidos.get(idxPedido).split("\\|")[0].trim().toUpperCase();
            controller.selecionarPedidoGlobal(idxPedido);
            
            boolean voltarListaPedidos = false;
            while (!voltarListaPedidos) {
                System.out.println("\n📋 DETALHES DO " + tituloPedido + " (Itens)");
                List<String> linhas = controller.getLinhasPedidoSelecionadoFormatadas();
                Integer idxLinha = escolher("Selecione um Item para ver tarefas em falta", linhas);
                
                if (idxLinha == null) { 
                    voltarListaPedidos = true; 
                } else {
                    System.out.println("\n🔨 TAREFAS EM FALTA PARA ESTE ITEM:");
                    List<String> tarefasEmFalta = controller.getDetalhesTarefasEmFaltaDaLinha(idxLinha);
                    if (tarefasEmFalta.isEmpty()) { System.out.println("✅ Este item já não tem tarefas pendentes."); }
                    else { tarefasEmFalta.forEach(t -> System.out.println("   -> " + t)); }
                    esperarEnter();
                }
            }
        }
    }

    // --- UI Helpers ---
    private void mostrarCabecalho() {
        System.out.println("\n\n");
        System.out.println("#########################################");
        System.out.println("#         SISTEMA DE PRODUÇÃO           #");
        System.out.println("#########################################");
    }
    private void separador() { System.out.println("-----------------------------------------"); }
    private Integer escolher(String titulo, List<String> opcoes) {
        System.out.println("\n>>> " + titulo + " <<<");
        for (int i = 0; i < opcoes.size(); i++) { System.out.printf("%d. %s%n", i + 1, opcoes.get(i)); }
        int escolha = lerInt("👉 Opção (0 para voltar): ");
        if (escolha <= 0 || escolha > opcoes.size()) return null;
        return escolha - 1;
    }
    private Integer lerInt(String msg) {
        try { System.out.print(msg); String l = scanner.nextLine(); if (l.trim().isEmpty()) return 0; return Integer.parseInt(l.trim()); } catch (Exception e) { return -1; }
    }
    private void esperarEnter() { System.out.println("\n(Pressione ENTER para continuar...)"); scanner.nextLine(); }
}