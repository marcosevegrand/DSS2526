package dss2526.ui.view;

import dss2526.ui.controller.GestaoController;
import dss2526.ui.controller.ProducaoController;
import dss2526.ui.controller.VendaController;
import dss2526.domain.enumeration.EstacaoTrabalho;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Janela Principal que agrega todas as funcionalidades do sistema.
 * Substitui a lógica de navegação que estava espalhada no App.java.
 */
public class MainView extends JPanel {

    // Referências aos controladores
    private final VendaController vendaCtrl;
    private final ProducaoController producaoCtrl;
    private final GestaoController estatsCtrl;

    // Componentes de Layout
    private CardLayout contentLayout;
    private JPanel contentPanel;
    private JPanel menuPanel;

    // Cores do Tema
    private final Color MENU_BG = new Color(30, 30, 35);
    private final Color CONTENT_BG = new Color(245, 245, 250);
    private final Color BTN_HOVER = new Color(50, 50, 55);

    public MainView(VendaController vendaCtrl, ProducaoController producaoCtrl, GestaoController estatsCtrl) {
        this.vendaCtrl = vendaCtrl;
        this.producaoCtrl = producaoCtrl;
        this.estatsCtrl = estatsCtrl;

        inicializarLayout();
    }

    private void inicializarLayout() {
        setLayout(new BorderLayout());

        // --- 1. Menu Lateral (Sidebar) ---
        menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(MENU_BG);
        menuPanel.setPreferredSize(new Dimension(250, 0));
        menuPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        // Título / Logo no Menu
        JLabel logo = new JLabel("<html><center>🍔<br>DSS FOOD</center></html>", SwingConstants.CENTER);
        logo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        logo.setForeground(Color.WHITE);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        logo.setBorder(new EmptyBorder(0, 0, 40, 0));
        menuPanel.add(logo);

        // Botões de Navegação
        adicionarBotaoMenu("🏠 Início", "HOME");
        adicionarBotaoMenu("🛒 Vendas", "VENDAS");
        adicionarBotaoMenu("👨‍🍳 Cozinha", "COZINHA_SELECAO"); // Abre seletor primeiro
        adicionarBotaoMenu("📊 Gestão", "GESTAO");

        menuPanel.add(Box.createVerticalGlue()); // Empurra rodapé para baixo
        JLabel footer = new JLabel("v1.0.0", SwingConstants.CENTER);
        footer.setForeground(Color.GRAY);
        footer.setAlignmentX(Component.CENTER_ALIGNMENT);
        menuPanel.add(footer);
        menuPanel.add(Box.createVerticalStrut(20));

        add(menuPanel, BorderLayout.WEST);

        // --- 2. Área de Conteúdo (Cards) ---
        contentLayout = new CardLayout();
        contentPanel = new JPanel(contentLayout);
        contentPanel.setBackground(CONTENT_BG);

        // Instanciar e adicionar as Views Filhas
        contentPanel.add(criarPainelHome(), "HOME");
        contentPanel.add(new TerminalVendaView(vendaCtrl), "VENDAS");
        contentPanel.add(criarSeletorEstacao(), "COZINHA_SELECAO"); // Painel intermédio
        contentPanel.add(new DashboardGestaoView(estatsCtrl), "GESTAO");

        add(contentPanel, BorderLayout.CENTER);
    }

    /**
     * Cria um botão estilizado para o menu lateral.
     */
    private void adicionarBotaoMenu(String texto, String cardName) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        btn.setForeground(Color.WHITE);
        btn.setBackground(MENU_BG);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setMaximumSize(new Dimension(250, 60));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(new EmptyBorder(10, 30, 10, 0));

        // Efeito Hover
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { btn.setBackground(BTN_HOVER); }
            public void mouseExited(java.awt.event.MouseEvent evt) { btn.setBackground(MENU_BG); }
        });

        btn.addActionListener(e -> contentLayout.show(contentPanel, cardName));
        menuPanel.add(btn);
    }

    /**
     * Painel Inicial (Dashboard Home)
     */
    private JPanel criarPainelHome() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(CONTENT_BG);
        JLabel bemVindo = new JLabel("<html><div style='text-align: center;'>Bem-vindo ao Sistema<br>Selecione uma opção no menu.</div></html>");
        bemVindo.setFont(new Font("Segoe UI", Font.PLAIN, 28));
        bemVindo.setForeground(Color.GRAY);
        p.add(bemVindo);
        return p;
    }

    /**
     * Painel intermédio para escolher a estação antes de ver o KDS.
     * Necessário porque o TerminalProducaoView precisa de uma estação específica.
     */
    private JPanel criarSeletorEstacao() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(CONTENT_BG);
        
        JPanel box = new JPanel(new GridLayout(0, 1, 10, 10));
        box.setBackground(Color.WHITE);
        box.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            new EmptyBorder(30, 50, 30, 50)
        ));

        JLabel lbl = new JLabel("Selecionar Estação de Trabalho:");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
        box.add(lbl);

        for (EstacaoTrabalho estacao : EstacaoTrabalho.values()) {
            JButton b = new JButton(estacao.toString());
            b.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            b.setBackground(new Color(230, 240, 255));
            b.addActionListener(e -> abrirCozinha(estacao));
            box.add(b);
        }

        p.add(box);
        return p;
    }

    private void abrirCozinha(EstacaoTrabalho estacao) {
        // Remove painel antigo se existir para recriar com a nova estação
        // Nota: Em produção real, poderias ter um cache de painéis
        for(Component c : contentPanel.getComponents()) {
            if ("COZINHA_ATIVA".equals(c.getName())) {
                contentPanel.remove(c);
            }
        }
        
        TerminalProducaoView view = new TerminalProducaoView(producaoCtrl, estacao);
        view.setName("COZINHA_ATIVA");
        contentPanel.add(view, "COZINHA_ATIVA");
        contentLayout.show(contentPanel, "COZINHA_ATIVA");
    }
}