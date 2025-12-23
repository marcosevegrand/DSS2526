package dss2526.ui.view;

import dss2526.ui.controller.GestaoController;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class DashboardGestaoView extends JPanel {
    
    private final GestaoController controller;
    
    public DashboardGestaoView(GestaoController controller) {
        this.controller = controller;
        setLayout(new BorderLayout());
        setBackground(MainView.CONTENT_BG);
        
        // Título
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT));
        header.setBackground(Color.WHITE);
        header.setBorder(new EmptyBorder(10,20,10,20));
        JLabel title = new JLabel("Painel de Gestão e Estatísticas");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        header.add(title);
        add(header, BorderLayout.NORTH);
        
        // Tabs
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabs.setBackground(Color.WHITE);
        
        tabs.addTab("Estatísticas", criarAbaEstatisticas());
        tabs.addTab("Gestão de Menu", criarAbaCRUD("Produtos", new String[]{"ID", "Nome", "Preço", "Categoria"}));
        tabs.addTab("Ingredientes", criarAbaCRUD("Ingredientes", new String[]{"ID", "Nome", "Stock", "Unidade"}));
        tabs.addTab("Funcionários", criarAbaCRUD("Staff", new String[]{"ID", "Nome", "Estação"}));
        
        add(tabs, BorderLayout.CENTER);
    }
    
    private JPanel criarAbaEstatisticas() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(MainView.CONTENT_BG);
        p.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Grid de KPIs
        JPanel grid = new JPanel(new GridLayout(2, 2, 20, 20));
        grid.setBackground(MainView.CONTENT_BG);
        
        // Valores Mockados (ou viriam do controller)
        grid.add(criarCardKPI("Faturação Hoje", "€ 1,240.50", new Color(52, 152, 219)));
        grid.add(criarCardKPI("Pedidos Atendidos", "84", new Color(46, 204, 113)));
        grid.add(criarCardKPI("Tempo Médio Espera", "12 min", new Color(241, 196, 15)));
        grid.add(criarCardKPI("Produtos em Baixo Stock", "3", new Color(231, 76, 60)));
        
        p.add(grid, BorderLayout.NORTH);
        
        // Área para gráficos futuros
        JLabel lblChart = new JLabel("<html><center><br><br>Gráficos de evolução semanal seriam desenhados aqui.</center></html>", SwingConstants.CENTER);
        lblChart.setForeground(Color.GRAY);
        p.add(lblChart, BorderLayout.CENTER);
        
        return p;
    }
    
    private JPanel criarCardKPI(String titulo, String valor, Color corTopo) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        
        // Barra colorida no topo
        JPanel topBar = new JPanel();
        topBar.setBackground(corTopo);
        topBar.setPreferredSize(new Dimension(10, 5));
        card.add(topBar, BorderLayout.NORTH);
        
        JPanel content = new JPanel(new GridLayout(2, 1));
        content.setBackground(Color.WHITE);
        content.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JLabel lblTit = new JLabel(titulo);
        lblTit.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblTit.setForeground(Color.GRAY);
        
        JLabel lblVal = new JLabel(valor);
        lblVal.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblVal.setForeground(MainView.TEXT_PRIMARY);
        
        content.add(lblTit);
        content.add(lblVal);
        card.add(content, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel criarAbaCRUD(String entidade, String[] colunas) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        toolbar.setBackground(Color.WHITE);
        JButton btnAdd = new JButton("Adicionar " + entidade);
        JButton btnEdit = new JButton("Editar");
        JButton btnDel = new JButton("Remover");
        toolbar.add(btnAdd);
        toolbar.add(btnEdit);
        toolbar.add(btnDel);
        p.add(toolbar, BorderLayout.NORTH);
        
        // Tabela
        DefaultTableModel model = new DefaultTableModel(colunas, 0);
        JTable table = new JTable(model);
        table.setRowHeight(25);
        
        // Mock Data
        if(entidade.equals("Produtos")) {
            model.addRow(new Object[]{"1", "Hambúrguer", "5.50", "Comida"});
            model.addRow(new Object[]{"2", "Cola", "1.50", "Bebida"});
        }
        
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        
        // Listeners simples
        btnAdd.addActionListener(e -> JOptionPane.showMessageDialog(this, "Janela de criação para " + entidade));
        btnDel.addActionListener(e -> {
            if(table.getSelectedRow() != -1) model.removeRow(table.getSelectedRow());
        });
        
        return p;
    }
}