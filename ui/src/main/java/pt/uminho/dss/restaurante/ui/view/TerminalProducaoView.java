// ...existing code...
package pt.uminho.dss.restaurante.ui.view;

import pt.uminho.dss.restaurante.ui.controller.ProducaoController;
import pt.uminho.dss.restaurante.domain.entity.Tarefa;
import pt.uminho.dss.restaurante.domain.enumeration.EstacaoTrabalho;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Painel simples para o terminal de produção.
 * Mostra tarefas pendentes para uma estação e permite marcar como concluídas.
 */
public class TerminalProducaoView extends JPanel {

    private final ProducaoController controller;
    private final EstacaoTrabalho estacao;
    private final DefaultListModel<String> listModel = new DefaultListModel<>();
    private final JList<String> lista = new JList<>(listModel);
    // Mapa índice -> id da tarefa
    private final Map<Integer, Long> indexToId = new HashMap<>();

    public TerminalProducaoView(ProducaoController controller, EstacaoTrabalho estacao) {
        this.controller = controller;
        this.estacao = estacao;
        inicializarUI();
        atualizarLista();
    }

    private void inicializarUI() {
        setLayout(new BorderLayout(8, 8));
        JLabel title = new JLabel("Terminal Produção - " + estacao, SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        add(title, BorderLayout.NORTH);

        lista.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scroll = new JScrollPane(lista);
        add(scroll, BorderLayout.CENTER);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnRefresh = new JButton("Atualizar");
        JButton btnConcluir = new JButton("Marcar como Concluída");

        btnRefresh.addActionListener(e -> atualizarLista());
        btnConcluir.addActionListener(e -> concluirSelecionada());

        controls.add(btnRefresh);
        controls.add(btnConcluir);
        add(controls, BorderLayout.SOUTH);
    }

    private void atualizarLista() {
        listModel.clear();
        indexToId.clear();
        List<Tarefa> tarefas = controller.listarTarefasPorEstacao(estacao);
        int idx = 0;
        for (Tarefa t : tarefas) {
            String text = String.format("#%d - %s %s", t.getId(),
                t.getProduto() != null ? t.getProduto().getNome() : "(sem produto)",
                t.getPedido() != null ? " (Pedido #" + t.getPedido().getId() + ")" : "");
            listModel.addElement(text);
            indexToId.put(idx++, t.getId());
        }
    }

    private void concluirSelecionada() {
        int sel = lista.getSelectedIndex();
        if (sel < 0) {
            JOptionPane.showMessageDialog(this, "Selecione uma tarefa primeiro.");
            return;
        }
        Long id = indexToId.get(sel);
        controller.concluirTarefa(id);
        JOptionPane.showMessageDialog(this, "Tarefa #" + id + " marcada como concluída.");
        atualizarLista();
    }
}