package dss2526.ui.view;

import dss2526.ui.controller.ProducaoController;
import dss2526.domain.enumeration.EstacaoTrabalho;
import dss2526.domain.entity.Tarefa;
import dss2526.domain.entity.Pedido;
import dss2526.domain.entity.Produto;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList;

/**
 * Interface tipo "Kanban" para os cozinheiros.
 */
public class TerminalProducaoView extends JPanel {

    private final ProducaoController controller;
    private final EstacaoTrabalho estacaoAtual;
    private JPanel containerTarefas;
    private Timer autoRefresh;

    public TerminalProducaoView(ProducaoController controller, EstacaoTrabalho estacao) {
        this.controller = controller;
        this.estacaoAtual = estacao;
        
        setLayout(new BorderLayout());
        setBackground(MainView.CONTENT_BG);
        
        inicializarHeader();
        inicializarAreaTarefas();
        
        // Timer para simular "Receber novas tarefas automaticamente" (Poll a cada 5 seg)
        autoRefresh = new Timer(5000, e -> carregarTarefas());
        autoRefresh.start();
        
        // Carregamento inicial
        carregarTarefas();
    }
    
    @Override
    public void removeNotify() {
        super.removeNotify();
        if(autoRefresh != null) autoRefresh.stop(); // Parar timer ao sair da tela
    }

    private void inicializarHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        JLabel lblTitulo = new JLabel("Estação: " + estacaoAtual.toString().replace("_", " "));
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(MainView.MENU_BG);
        
        JButton btnRefresh = new JButton("🔄 Atualizar");
        btnRefresh.addActionListener(e -> carregarTarefas());
        
        header.add(lblTitulo, BorderLayout.WEST);
        header.add(btnRefresh, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);
    }
    
    private void inicializarAreaTarefas() {
        // FlowLayout alinhado à esquerda para cards
        containerTarefas = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        containerTarefas.setBackground(MainView.CONTENT_BG);
        containerTarefas.setBorder(new EmptyBorder(10,10,10,10));
        
        JScrollPane scroll = new JScrollPane(containerTarefas);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);
    }
    
    private void carregarTarefas() {
        containerTarefas.removeAll();
        
        List<Tarefa> tarefas = new ArrayList<>();
        if(controller != null) {
            tarefas = controller.listarTarefasPorEstacao(estacaoAtual);
        }
        
        // Mock se vazio para demonstração
        if(tarefas.isEmpty()) {
            criarTarefaMock(101, "Cheeseburger", "Sem pickles");
            criarTarefaMock(102, "Batata Frita", "");
            criarTarefaMock(103, "Grelhar Bife", "Bem passado");
        } else {
            for(Tarefa t : tarefas) {
                criarCardTarefa(t);
            }
        }
        
        containerTarefas.revalidate();
        containerTarefas.repaint();
    }
    
    private void criarTarefaMock(int idPedido, String produto, String nota) {
        // Criar objetos dummy só para a UI desenhar
        Pedido p = new Pedido(); p.setId(idPedido);
        Produto prod = new Produto(); prod.setNome(produto);
        Tarefa t = new Tarefa();
        t.setPedido(p);
        t.setProduto(prod);
        // t.setNota(nota); // Assumindo que Tarefa tem nota
        criarCardTarefa(t);
    }

    private void criarCardTarefa(Tarefa t) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(280, 200));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200,200,200), 1),
            new EmptyBorder(10, 15, 10, 15)
        ));
        
        // 1. Cabeçalho do Card (ID Pedido e Tempo)
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setMaximumSize(new Dimension(300, 30));
        JLabel lblId = new JLabel("#" + t.getPedido().getId());
        lblId.setFont(new Font("Monospaced", Font.BOLD, 18));
        header.add(lblId, BorderLayout.WEST);
        card.add(header);
        card.add(Box.createVerticalStrut(10));
        
        // 2. Produto
        JLabel lblProd = new JLabel("<html><b>" + t.getProduto().getNome() + "</b></html>");
        lblProd.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        lblProd.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(lblProd);
        
        // 3. Notas (Opcional)
        // String nota = t.getNota(); 
        String nota = "Nota ex."; // Mock
        if(nota != null && !nota.isEmpty()) {
            JLabel lblNota = new JLabel("<html><font color='red'>⚠️ " + nota + "</font></html>");
            lblNota.setBorder(new EmptyBorder(5,0,5,0));
            card.add(lblNota);
        }
        
        card.add(Box.createVerticalGlue());
        
        // 4. Botões de Ação
        JPanel actions = new JPanel(new GridLayout(1, 2, 10, 0));
        actions.setBackground(Color.WHITE);
        actions.setMaximumSize(new Dimension(300, 40));
        
        JButton btnAtraso = new JButton("Atraso");
        btnAtraso.setBackground(new Color(241, 196, 15));
        btnAtraso.setFocusPainted(false);
        btnAtraso.addActionListener(e -> marcarAtraso(t));
        
        JButton btnPronto = new JButton("Pronto");
        btnPronto.setBackground(new Color(46, 204, 113));
        btnPronto.setForeground(Color.WHITE);
        btnPronto.setFocusPainted(false);
        btnPronto.addActionListener(e -> marcarTerminado(t, card));
        
        actions.add(btnAtraso);
        actions.add(btnPronto);
        card.add(actions);
        
        containerTarefas.add(card);
    }
    
    private void marcarTerminado(Tarefa t, JPanel card) {
        // controller.concluirTarefa(t);
        // Efeito visual de remoção
        containerTarefas.remove(card);
        containerTarefas.revalidate();
        containerTarefas.repaint();
        JOptionPane.showMessageDialog(this, "Tarefa concluída para o pedido #" + t.getPedido().getId());
    }
    
    private void marcarAtraso(Tarefa t) {
        // controller.reportarAtraso(t);
        JOptionPane.showMessageDialog(this, "Atraso reportado ao gestor!");
    }
}