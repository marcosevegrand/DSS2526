package dss2526.ui.view;

import dss2526.ui.controller.*;
import dss2526.domain.enumeration.EstacaoTrabalho;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * Janela Principal que agrega todas as funcionalidades do sistema.
 * Utiliza um CardLayout para alternar entre os módulos.
 */
public class MainView extends JPanel {

    // Controladores
    private final VendaController vendaCtrl;
    private final ProducaoController producaoCtrl;
    private final GestaoController gestaoCtrl;

    // Componentes de Layout
    private CardLayout contentLayout;
    private JPanel contentPanel;
    private JPanel menuPanel;
    
    // Gestão de Botões do Menu
    private Map<String, JPanel> menuButtons = new HashMap<>();
    private String currentCard = "HOME";

    // --- DESIGN SYSTEM (Cores) ---
    public static final Color MENU_BG = new Color(44, 62, 80);       // Azul Escuro
    public static final Color MENU_HOVER = new Color(52, 73, 94);    // Azul Escuro Claro
    public static final Color MENU_ACTIVE = new Color(46, 204, 113); // Verde Destaque
    public static final Color CONTENT_BG = new Color(236, 240, 241); // Cinza Claro Fundo
    public static final Color TEXT_PRIMARY = new Color(44, 62, 80);
    public static final Color ACCENT_COLOR = new Color(52, 152, 219); // Azul Botões

    public MainView(VendaController vendaCtrl, ProducaoController producaoCtrl, GestaoController gestaoCtrl) {
        this.vendaCtrl = vendaCtrl;
        this.producaoCtrl = producaoCtrl;
        this.gestaoCtrl = gestaoCtrl;

        inicializarLayout();
    }

    private void inicializarLayout() {
        setLayout(new BorderLayout());

        // --- 1. Menu Lateral (Sidebar) ---
        menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(MENU_BG);
        menuPanel.setPreferredSize(new Dimension(260, 0));
        
        // Logo / Título
        JPanel logoPanel = new JPanel(new BorderLayout());
        logoPanel.setBackground(MENU_BG);
        logoPanel.setBorder(new EmptyBorder(30, 20, 30, 20));
        JLabel logo = new JLabel("<html><font color='white'><b>DSS</b> FOOD</font></html>");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        logo.setIcon(new JLabel("🍔").getIcon()); 
        logoPanel.add(logo, BorderLayout.CENTER);
        menuPanel.add(logoPanel);
        
        // Separador
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(255,255,255,50));
        sep.setMaximumSize(new Dimension(220, 1));
        menuPanel.add(sep);
        menuPanel.add(Box.createVerticalStrut(20));

        // Botões de Navegação
        adicionarBotaoMenu("Início", "🏠", "HOME");
        adicionarBotaoMenu("Novo Pedido", "🛒", "VENDAS");
        adicionarBotaoMenu("Cozinha (KDS)", "👨‍🍳", "COZINHA_SELECAO");
        adicionarBotaoMenu("Backoffice", "📊", "GESTAO");

        menuPanel.add(Box.createVerticalGlue());
        
        // Rodapé
        JLabel footer = new JLabel("v1.0 DSS Project", SwingConstants.CENTER);
        footer.setForeground(Color.GRAY);
        footer.setAlignmentX(Component.CENTER_ALIGNMENT);
        menuPanel.add(footer);
        menuPanel.add(Box.createVerticalStrut(15));

        add(menuPanel, BorderLayout.WEST);

        // --- 2. Área de Conteúdo ---
        contentLayout = new CardLayout();
        contentPanel = new JPanel(contentLayout);
        contentPanel.setBackground(CONTENT_BG);

        // Instanciar Views
        contentPanel.add(criarPainelHome(), "HOME");
        contentPanel.add(new TerminalVendaView(vendaCtrl), "VENDAS");
        contentPanel.add(criarSeletorEstacao(), "COZINHA_SELECAO");
        contentPanel.add(new DashboardGestaoView(gestaoCtrl), "GESTAO");

        add(contentPanel, BorderLayout.CENTER);
        
        // Estado inicial
        atualizarEstiloMenu("HOME");
    }

    private void adicionarBotaoMenu(String texto, String icone, String cardName) {
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        btnPanel.setBackground(MENU_BG);
        btnPanel.setMaximumSize(new Dimension(260, 60));
        btnPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JLabel lblIcon = new JLabel(icone);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        lblIcon.setForeground(Color.LIGHT_GRAY);
        
        JLabel lblText = new JLabel(texto);
        lblText.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblText.setForeground(Color.LIGHT_GRAY);
        
        btnPanel.add(lblIcon);
        btnPanel.add(lblText);

        btnPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Se for cozinha, reseta para seleção
                if(cardName.equals("COZINHA_SELECAO")) {
                    contentLayout.show(contentPanel, "COZINHA_SELECAO");
                } else {
                    contentLayout.show(contentPanel, cardName);
                }
                atualizarEstiloMenu(cardName);
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!cardName.equals(currentCard)) btnPanel.setBackground(MENU_HOVER);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if (!cardName.equals(currentCard)) btnPanel.setBackground(MENU_BG);
            }
        });

        menuPanel.add(btnPanel);
        menuButtons.put(cardName, btnPanel);
    }
    
    private void atualizarEstiloMenu(String activeCard) {
        this.currentCard = activeCard;
        menuButtons.forEach((k, panel) -> {
            panel.setBackground(MENU_BG);
            panel.getComponent(0).setForeground(Color.LIGHT_GRAY); // Icone
            panel.getComponent(1).setForeground(Color.LIGHT_GRAY); // Texto
            panel.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
        });

        JPanel active = menuButtons.get(activeCard);
        if (active != null) {
            active.setBackground(MENU_HOVER);
            active.getComponent(0).setForeground(Color.WHITE);
            active.getComponent(1).setForeground(Color.WHITE);
            active.setBorder(BorderFactory.createMatteBorder(0, 5, 0, 0, MENU_ACTIVE));
        }
    }

    // --- Páginas Auxiliares ---

    private JPanel criarPainelHome() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(CONTENT_BG);
        
        JLabel titulo = new JLabel("<html><div style='text-align: center; color: #555;'>"
                + "<span style='font-size: 32px;'>Bem-vindo ao <b>DSS FOOD</b></span><br><br>"
                + "<span style='font-size: 16px;'>Selecione um módulo no menu lateral para começar.</span>"
                + "</div></html>");
        p.add(titulo);
        return p;
    }
    
    private JPanel criarSeletorEstacao() {
        JPanel container = new JPanel(new GridBagLayout());
        container.setBackground(CONTENT_BG);
        
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220,220,220)),
            new EmptyBorder(40, 60, 40, 60)
        ));
        
        JLabel lbl = new JLabel("Selecionar Estação de Trabalho");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(lbl);
        card.add(Box.createVerticalStrut(30));

        for (EstacaoTrabalho estacao : EstacaoTrabalho.values()) {
            JButton b = new JButton(estacao.toString().replace("_", " "));
            b.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            b.setBackground(new Color(240, 248, 255));
            b.setMaximumSize(new Dimension(300, 50));
            b.setAlignmentX(Component.CENTER_ALIGNMENT);
            b.setFocusPainted(false);
            
            b.addActionListener(e -> abrirCozinha(estacao));
            
            card.add(b);
            card.add(Box.createVerticalStrut(10));
        }
        
        container.add(card);
        return container;
    }

    private void abrirCozinha(EstacaoTrabalho estacao) {
        // Remove painel antigo de cozinha se existir
        for(Component c : contentPanel.getComponents()) {
            if ("COZINHA_ATIVA".equals(c.getName())) contentPanel.remove(c);
        }
        
        TerminalProducaoView view = new TerminalProducaoView(producaoCtrl, estacao);
        view.setName("COZINHA_ATIVA");
        contentPanel.add(view, "COZINHA_ATIVA");
        contentLayout.show(contentPanel, "COZINHA_ATIVA");
    }
}