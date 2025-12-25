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
    
    // Corrigido: Nome da variável sem espaços
    private int restauranteIdAtual = -1;

    public ProducaoUI(IProducaoFacade producaoFacade) {
        this.producaoFacade = producaoFacade;
        this.scanner = new Scanner(System.in);
    }

    public void run() {
        System.out.println("\n=== Acesso ao Terminal de Produção ===");
        // Corrigido: Atribuição à variável correta
        this.restauranteIdAtual = lerInteiro("Introduza o ID do Restaurante: ");
        
        verificarAvisos();

        List<Estacao> estacoes = producaoFacade.listarEstacoesPorRestaurante(restauranteIdAtual);
        
        if (estacoes.isEmpty()) {
            System.out.println("Nenhuma estação configurada para este restaurante.");
            return;
        }

        String[] nomesEstacoes = estacoes.stream()
                .map(e -> e.getTrabalho().toString())
                .toArray(String[]::new);

        NewMenu menuEstacoes = new NewMenu("Selecione a Estação de Trabalho", nomesEstacoes);

        for (int i = 0; i < estacoes.size(); i++) {
            Estacao est = estacoes.get(i);
            // Definimos o handler para abrir o sub-menu da estação
            menuEstacoes.setHandler(i + 1, () -> gerirTarefasEstacao(est.getTrabalho()));
        }

        menuEstacoes.run();
    }

    private void gerirTarefasEstacao(Trabalho tipoEstacao) {
        boolean[] voltar = {false}; // Usamos array para permitir alteração dentro da lambda
        
        while (!voltar[0]) {
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

            menuTarefa.setHandler(1, () -> {}); 
            menuTarefa.setHandler(2, () -> iniciarTarefa(tarefas));
            menuTarefa.setHandler(3, () -> concluirTarefa(tarefas));
            menuTarefa.setHandler(4, () -> reportarStock(tarefas));
            menuTarefa.setHandler(5, () -> voltar[0] = true); // Altera o estado para sair do loop

            menuTarefa.run();
        }
    }

    // --- Ações de Produção ---

    private void iniciarTarefa(List<Tarefa> tarefas) {
        if (tarefas.isEmpty()) {
            System.out.println("Não há tarefas disponíveis.");
            return;
        }
        int idx = lerInteiro("Índice da tarefa a iniciar: ") - 1;
        if (idx >= 0 && idx < tarefas.size()) {
            producaoFacade.iniciarTarefa(tarefas.get(idx).getId());
            System.out.println(">> Tarefa em preparação...");
        } else {
            System.out.println("Índice inválido.");
        }
    }

    private void concluirTarefa(List<Tarefa> tarefas) {
        if (tarefas.isEmpty()) {
            System.out.println("Não há tarefas para concluir.");
            return;
        }
        int idx = lerInteiro("Índice da tarefa concluída: ") - 1;
        if (idx >= 0 && idx < tarefas.size()) {
            producaoFacade.concluirTarefa(tarefas.get(idx).getId());
            System.out.println(">> Tarefa finalizada!");
        } else {
            System.out.println("Índice inválido.");
        }
    }

    private void reportarStock(List<Tarefa> tarefas) {
        if (tarefas.isEmpty()) return;
        int idx = lerInteiro("Tarefa afetada: ") - 1;
        if (idx >= 0 && idx < tarefas.size()) {
            int ingId = lerInteiro("ID do Ingrediente em falta: ");
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
}