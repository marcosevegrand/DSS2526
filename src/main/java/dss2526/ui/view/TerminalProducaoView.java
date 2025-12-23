package dss2526.ui.view;

import dss2526.ui.controller.ProducaoController;
import dss2526.domain.enumeration.EstacaoTrabalho;
import dss2526.domain.entity.Tarefa;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class TerminalProducaoView extends JPanel {

    public TerminalProducaoView(ProducaoController controller, EstacaoTrabalho estacao) {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 240, 245));
        
        // Título da Estação
        JLabel title = new JLabel("Estação: " + estacao, SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setBorder(new EmptyBorder(20,0,20,0));
        add(title, BorderLayout.NORTH);
        
        // Painel fluido para cartões de tarefas
        JPanel listaTarefas = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));
        listaTarefas.setOpaque(false);
        
        // Simular Cards de Tarefas vindos do controller
        for(Tarefa t : controller.listarTarefasPorEstacao(estacao)) {
            JPanel card = new JPanel(new BorderLayout());
            card.setPreferredSize(new Dimension(280, 180));
            card.setBackground(Color.WHITE);
            // Sombra simples (borda cinza)
            card.setBorder(BorderFactory.createLineBorder(new Color(200,200,200), 1));
            
            // Header do Card
            JLabel l1 = new JLabel("#PEDIDO " + t.getPedido().getId());
            l1.setFont(new Font("Monospaced", Font.BOLD, 16));
            l1.setOpaque(true);
            l1.setBackground(new Color(255, 243, 224)); // Laranja claro
            l1.setBorder(new EmptyBorder(10,10,10,10));
            
            // Conteúdo
            JLabel l2 = new JLabel("<html><center>"+t.getProduto().getNome()+"</center></html>", SwingConstants.CENTER);
            l2.setFont(new Font("Segoe UI", Font.PLAIN, 18));
            
            // Botão Ação
            JButton btnDone = new JButton("PRONTO ✅");
            btnDone.setBackground(new Color(46, 204, 113));
            btnDone.setForeground(Color.WHITE);
            btnDone.setFont(new Font("Segoe UI", Font.BOLD, 14));
            btnDone.addActionListener(e -> {
                card.setVisible(false); // Simula remoção visual
                JOptionPane.showMessageDialog(this, "Tarefa marcada como concluída!");
            });
            
            card.add(l1, BorderLayout.NORTH);
            card.add(l2, BorderLayout.CENTER);
            card.add(btnDone, BorderLayout.SOUTH);
            
            listaTarefas.add(card);
        }
        
        add(new JScrollPane(listaTarefas), BorderLayout.CENTER);
    }
}