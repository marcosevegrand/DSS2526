package dss2526.ui.view;

import dss2526.domain.entity.Estacao;
import dss2526.domain.entity.Tarefa;
import dss2526.domain.entity.Mensagem;
import dss2526.domain.enumeration.Trabalho;
import dss2526.producao.IProducaoFacade;
import dss2526.ui.delegate.NewMenu;

import java.util.List;
import java.util.Scanner;

public class ProducaoUI {

    private final IProducaoFacade producaoFacade;
    private final Scanner scanner;
    
    private int restauranteId Atual = -1;

    public ProducaoUI(IProducaoFacade producaoFacade) {
        this.producaoFacade = producaoFacade;
        this.scanner = new Scanner(System.in);
    }

    public void run() {
        System.out.println("\n=== Acesso ao Terminal de Produção ===");
        this.restauranteIdAtual = lerInteiro("Introduza o ID do Restaurante: ");
        
        // Antes de mostrar as tarefas, verifica se há avisos da gerência (OCC)
        verificarAvisos();

        // Lista as estações disponíveis neste restaurante
        List<Estacao> estacoes = producaoFacade.listarEstacoesPorRestaurante(restauranteIdAtual);
        
        if (estacoes.isEmpty()) {
            System.out.println("Nenhuma estação configurada para este restaurante.");
            return;
        }

        String[] nomesEstacoes = estacoes.stream()
                .map(e -> e.getTipoTrabalho().toString())
                .toArray(String[]::new);

        NewMenu menuEstacoes = new NewMenu("Selecione a Estação de Trabalho", nomesEstacoes);

        // Configura handlers dinamicamente para cada estação
        for (int i = 0; i < estacoes.size(); i++) {
            Estacao est = estacoes.get(i);
            menuEstacoes.setHandler(i + 1, () -> gerirTarefasEstacao(est.getTipoTrabalho()));
        }

        menuEstacoes.run();
    }

    /**
     * Loop de gestão de tarefas para uma estação específica (ex: GRELHADOS)
     */
    private void gerirTarefasEstacao(Trabalho tipoEstacao) {
        boolean voltar = false;
        while (!voltar) {
            verificarAvisos();
            List<Tarefa> tarefas = producaoFacade.obterTarefas(restauranteIdAtual, tipoEstacao);

            System.out.println("\n--- Fila de Produção: " + tipoEstacao + " ---");
            if (tarefas.isEmpty()) {
                System.out.println("[Fila Vazia]");
            } else {
                for (int i = 0; i < tarefas.size(); i++) {
                    Tarefa t = tarefas.get(i);
                    System.out.printf("%d. [Pedido #%d] %s\n", (i + 1), t.getPedidoId(), t.getNome());
                }
            }

            String[] opcoes = {"Atualizar Lista", "Iniciar Próxima Tarefa", "Concluir Tarefa", "Reportar Falta de Stock", "Voltar"};
            NewMenu menuTarefa = new NewMenu("Operações - " + tipoEstacao, opcoes);

            menuTarefa.setHandler(1, () -> {}); // Apenas refresca o loop
            menuTarefa.setHandler(2, () -> iniciarTarefa(tarefas));
            menuTarefa.setHandler(3, () -> concluirTarefa(tarefas));
            menuTarefa.setHandler(4, () -> reportarStock(tarefas));
            menuTarefa.setHandler(5, () -> { return true; }); // Sinal para sair do sub-menu

            voltar = menuTarefa.run();
        }
    }

    // --- Ações de Produção ---

    private void iniciarTarefa(List<Tarefa> tarefas) {
        if (tarefas.isEmpty()) return;
        int idx = lerInteiro("Índice da tarefa a iniciar: ") - 1;
        if (idx >= 0 && idx < tarefas.size()) {
            producaoFacade.iniciarTarefa(tarefas.get(idx).getId());
            System.out.println(">> Tarefa em preparação...");
        }
    }

    private void concluirTarefa(List<Tarefa> tarefas) {
        if (tarefas.isEmpty()) return;
        int idx = lerInteiro("Índice da tarefa concluída: ") - 1;
        if (idx >= 0 && idx < tarefas.size()) {
            producaoFacade.concluirTarefa(tarefas.get(idx).getId());
            System.out.println(">> Tarefa finalizada!");
        }
    }

    private void reportarStock(List<Tarefa> tarefas) {
        if (tarefas.isEmpty()) return;
        int idx = lerInteiro("Tarefa afetada: ") - 1;
        int ingId = lerInteiro("ID do Ingrediente em falta: ");
        
        if (idx >= 0 && idx < tarefas.size()) {
            producaoFacade.reportarFaltaIngrediente(tarefas.get(idx).getId(), ingId, restauranteIdAtual);
            System.out.println(">> Alerta enviado à gerência. Pedido colocado em espera.");
        }
    }

    private void verificarAvisos() {
        List<Mensagem> avisos = producaoFacade.lerAvisosPendentes(restauranteIdAtual);
        if (!avisos.isEmpty()) {
            System.out.println("\n!!! MENSAGENS DA GERÊNCIA !!!");
            for (Mensagem m : avisos) {
                String prefix = m.isUrgente() ? "[URGENTE] " : "[AVISO] ";
                System.out.println(prefix + m.getTexto());
            }
            System.out.println("-----------------------------\n");
        }
    }

    // --- Helpers de Input ---

    private int lerInteiro(String msg) {
        System.out.print(msg);
        while (!scanner.hasNextInt()) {
            scanner.next();
            System.out.print("Inválido. Introduza um número: ");
        }
        int res = scanner.nextInt();
        scanner.nextLine();
        return res;
    }

    private String lerString(String msg) {
        System.out.print(msg);
        return scanner.nextLine();
    }
}