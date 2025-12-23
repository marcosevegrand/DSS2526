package dss2526.ui.view;

import dss2526.ui.controller.GestaoController;
import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;

public class DashboardGestaoView extends JPanel {
    
    public DashboardGestaoView(GestaoController controller) {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        // Título
        JLabel title = new JLabel("Dashboard de Gestão", SwingConstants.LEFT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setBorder(new EmptyBorder(20, 20, 20, 20));
        add(title, BorderLayout.NORTH);
        
        // Tabs
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        
        tabs.addTab("Visão Geral", criarPainelIndicadores(controller));
        tabs.addTab("Produtos Top", criarPainelDummy("Lista de produtos mais vendidos..."));
        tabs.addTab("Performance", criarPainelDummy("Tempos médios de espera..."));
        
        add(tabs, BorderLayout.CENTER);
    }
    
    // Cria cartões simples com números (KPIs)
    private JPanel criarPainelIndicadores(GestaoController ctrl) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 30, 30));
        p.setBackground(Color.WHITE);
        
        p.add(criarKPI("Faturação Hoje", "€" + ctrl.faturacaoTotal(null, null), new Color(52, 152, 219)));
        p.add(criarKPI("Ticket Médio", "€" + ctrl.ticketMedio(null, null), new Color(155, 89, 182)));
        p.add(criarKPI("Cancelamentos", ctrl.taxaCancelamento(null, null) + "%", new Color(231, 76, 60)));
        
        return p;
    }
    
    private JPanel criarKPI(String titulo, String valor, Color cor) {
        JPanel kpi = new JPanel(new GridLayout(2, 1));
        kpi.setPreferredSize(new Dimension(200, 120));
        kpi.setBackground(cor);
        kpi.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        
        JLabel lblTit = new JLabel(titulo, SwingConstants.CENTER);
        lblTit.setForeground(new Color(255,255,255,200));
        lblTit.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        
        JLabel lblVal = new JLabel(valor, SwingConstants.CENTER);
        lblVal.setForeground(Color.WHITE);
        lblVal.setFont(new Font("Segoe UI", Font.BOLD, 32));
        
        kpi.add(lblTit);
        kpi.add(lblVal);
        return kpi;
    }
    
    private JPanel criarPainelDummy(String msg) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Color.WHITE);
        JLabel l = new JLabel(msg);
        l.setFont(new Font("Segoe UI", Font.ITALIC, 20));
        l.setForeground(Color.GRAY);
        p.add(l);
        return p;
    }
}