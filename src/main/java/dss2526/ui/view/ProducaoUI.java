package dss2526.ui.view;

import dss2526.domain.entity.Mensagem;
import dss2526.domain.entity.Tarefa;
import dss2526.ui.controller.ProducaoController;
import dss2526.ui.util.NewMenu;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class ProducaoUI {
    private final ProducaoController controller;
    private final Scanner scanner;

    public ProducaoUI(ProducaoController controller) {
        this.controller = controller;
        this.scanner = new Scanner(System.in);
    }

    public void show() {
        // No início, o funcionário escolhe onde está a trabalhar
        List<String> restaurantes = controller.listarNomesRestaurantes();
        Integer restIdx = escolher("Restaurante", restaurantes);
        if (restIdx == null) return;

        List<String> estacoes = controller.listarNomesEstacoes(restIdx);
        Integer estIdx = escolher("Estação de Trabalho", estacoes);
        if (estIdx == null) return;

        // Inicia a sessão no controller
        controller.selecionarContexto(restIdx, estIdx);

        NewMenu menu = new NewMenu("--- Terminal de Produção ---", new String[]{
            "Consultar Fila de Trabalho",
            "Ver Mensagens da Gestão",
            "Sinalizar Falta de Ingrediente"
        });

        menu.setHandler(1, this::gerirTarefas);
        menu.setHandler(2, this::mostrarMensagens);
        menu.setHandler(3, this::solicitarIngrediente);

        menu.run();
    }

    private void gerirTarefas() {
        List<Tarefa> tarefas = controller.getFilaTrabalho();
        
        if (tarefas.isEmpty()) {
            System.out.println("\nSem tarefas pendentes para esta estação.");
            return;
        }

        // Transforma a lista de objetos Tarefa numa lista de Strings para o método escolher
        List<String> descricoes = tarefas.stream()
            .map(t -> String.format("[Pedido #%d] Item ID: %d - Criada em: %s", 
                     t.getPedidoId(), t.getProdutoId(), t.getDataCriacao().toLocalTime()))
            .collect(Collectors.toList());

        Integer tIdx = escolher("Tarefa para Concluir", descricoes);
        
        if (tIdx != null) {
            int idReal = tarefas.get(tIdx).getId();
            controller.concluirTarefa(idReal);
            System.out.println("Tarefa concluída com sucesso!");
        }
    }

    private void mostrarMensagens() {
        List<Mensagem> mensagens = controller.getMensagensGestao();
        System.out.println("\n--- Mensagens Recentes ---");
        if (mensagens.isEmpty()) {
            System.out.println("Nenhuma mensagem recebida.");
        } else {
            mensagens.forEach(m -> System.out.printf("[%s] %s%n", 
                m.getDataHora().toLocalTime(), m.getTexto()));
        }
    }

    private void solicitarIngrediente() {
        // Aqui o funcionário indica o ID do ingrediente que acabou
        int ingId = lerInt("Introduza o ID do ingrediente em falta: ");
        controller.solicitarIngrediente(ingId);
        System.out.println("Alerta de stock enviado para a gestão.");
    }

    // --- Métodos Auxiliares (Seguindo o estilo da VendaUI) ---

    private Integer escolher(String titulo, List<String> opcoes) {
        System.out.printf("\n--- Escolher %s ---\n", titulo);
        for (int i = 0; i < opcoes.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, opcoes.get(i));
        }
        int escolha = lerInt(String.format("Escolha um %s (0 para cancelar): ", titulo));
        if (escolha == 0) return null;
        return escolha - 1;
    }

    private Integer lerInt(String msg) {
        System.out.print(msg);
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private String lerString(String msg) {
        System.out.print(msg);
        return scanner.nextLine();
    }
}