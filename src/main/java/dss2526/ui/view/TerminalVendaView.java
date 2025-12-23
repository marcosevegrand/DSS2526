package dss2526.ui.view;

import dss2526.ui.controller.VendaController;
import dss2526.domain.entity.Pedido;
import dss2526.domain.entity.Produto;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TerminalVendaView extends JPanel {
    
    private final VendaController vendaController;
    private Pedido pedidoAtual;
    
    // Componentes UI
    private DefaultTableModel cartModel;
    private JTable cartTable;
    private JLabel totalLabel;
    private JPanel gridProdutos;
    
    // Cores Locais
    private final Color PRIMARY = new Color(52, 152, 219);
    private final Color DANGER = new Color(231, 76, 60);
    private final Color SUCCESS = new Color(46, 204, 113);
    
    public TerminalVendaView(VendaController vendaController) {
        this.vendaController = vendaController;
        setLayout(new BorderLayout());
        setBackground(MainView.CONTENT_BG);
        
        // Inicializa pedido vazio ou busca do controller
        try {
            this.pedidoAtual = vendaController.novoPedido(); 
        } catch (Exception e) {
            this.pedidoAtual = new Pedido(); // Fallback
        }

        inicializarUI();
    }
    
    private void inicializarUI() {
        // --- COLUNA ESQUERDA (Produtos) ---
        JPanel panelProdutos = new JPanel(new BorderLayout());
        panelProdutos.setBackground(MainView.CONTENT_BG);
        panelProdutos.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Categorias (Topo)
        JPanel categoriasPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        categoriasPanel.setBackground(MainView.CONTENT_BG);
        String[] cats = {"Todos", "Menus", "Bebidas", "Sobremesas"};
        for(String c : cats) {
            JButton btn = new JButton(c);
            btn.setBackground(Color.WHITE);
            btn.setFocusPainted(false);
            categoriasPanel.add(btn);
            // Listener simples para simular filtro
            btn.addActionListener(e -> carregarProdutos(c));
        }
        panelProdutos.add(categoriasPanel, BorderLayout.NORTH);
        
        // Grid (Centro)
        gridProdutos = new JPanel(new GridLayout(0, 3, 10, 10)); // 3 colunas
        gridProdutos.setBackground(MainView.CONTENT_BG);
        JScrollPane scrollGrid = new JScrollPane(gridProdutos);
        scrollGrid.setBorder(null);
        panelProdutos.add(scrollGrid, BorderLayout.CENTER);
        
        // Carregar produtos iniciais
        carregarProdutos("Todos");

        // --- COLUNA DIREITA (Carrinho) ---
        JPanel panelCarrinho = new JPanel(new BorderLayout());
        panelCarrinho.setPreferredSize(new Dimension(400, 0));
        panelCarrinho.setBackground(Color.WHITE);
        panelCarrinho.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, Color.LIGHT_GRAY));
        
        // Header Carrinho
        JLabel lblCart = new JLabel("Pedido Atual", SwingConstants.CENTER);
        lblCart.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblCart.setBorder(new EmptyBorder(20, 0, 20, 0));
        panelCarrinho.add(lblCart, BorderLayout.NORTH);
        
        // Tabela
        String[] colunas = {"Item", "Qtd", "Preço"};
        cartModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        cartTable = new JTable(cartModel);
        cartTable.setRowHeight(30);
        cartTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cartTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        cartTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        panelCarrinho.add(new JScrollPane(cartTable), BorderLayout.CENTER);
        
        // Área de Totais e Ações
        JPanel footer = new JPanel();
        footer.setLayout(new BoxLayout(footer, BoxLayout.Y_AXIS));
        footer.setBackground(Color.WHITE);
        footer.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        totalLabel = new JLabel("Total: € 0.00");
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        totalLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Botões de Ação
        JPanel btnPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        btnPanel.setBackground(Color.WHITE);
        btnPanel.setBorder(new EmptyBorder(20,0,0,0));
        
        JButton btnNota = createBtn("📝 Nota", Color.ORANGE);
        btnNota.addActionListener(e -> adicionarNota());
        
        JButton btnRemove = createBtn("❌ Remover", DANGER);
        btnRemove.addActionListener(e -> removerItemSelecionado());
        
        JButton btnCancel = createBtn("🗑 Cancelar", Color.GRAY);
        btnCancel.addActionListener(e -> cancelarPedido());
        
        JButton btnPay = createBtn("✅ Confirmar", SUCCESS);
        btnPay.addActionListener(e -> finalizarPedido());
        
        btnPanel.add(btnNota);
        btnPanel.add(btnRemove);
        btnPanel.add(btnCancel);
        btnPanel.add(btnPay);
        
        footer.add(totalLabel);
        footer.add(btnPanel);
        panelCarrinho.add(footer, BorderLayout.SOUTH);

        // Adiciona painéis principais
        add(panelProdutos, BorderLayout.CENTER);
        add(panelCarrinho, BorderLayout.EAST);
    }
    
    private void carregarProdutos(String filtro) {
        gridProdutos.removeAll();
        
        // Simulação de busca no controller
        // Na implementação real: List<Produto> lista = vendaController.getProdutos(filtro);
        List<Produto> lista = new ArrayList<>();
        if(vendaController != null) lista = vendaController.listarProdutos();
        
        // Se a lista estiver vazia (para teste de UI), cria fakes
        if(lista.isEmpty()) {
            criarProdutoFake("Hambúrguer Clássico", 5.50);
            criarProdutoFake("Cheeseburger", 6.00);
            criarProdutoFake("Batatas Fritas", 2.00);
            criarProdutoFake("Cola Zero", 1.50);
            criarProdutoFake("Gelado", 2.50);
        } else {
            for(Produto p : lista) {
                adicionarCardProduto(p);
            }
        }
        
        gridProdutos.revalidate();
        gridProdutos.repaint();
    }
    
    private void criarProdutoFake(String nome, double preco) {
        Produto p = new Produto();
        p.setNome(nome);
        p.setPreco(new java.math.BigDecimal(preco));
        adicionarCardProduto(p);
    }
    
    private void adicionarCardProduto(Produto p) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(new Color(220,220,220), 1));
        card.setPreferredSize(new Dimension(150, 150));
        
        // Icone e Nome
        JPanel center = new JPanel(new GridLayout(2,1));
        center.setBackground(Color.WHITE);
        JLabel icon = new JLabel("🍔", SwingConstants.CENTER); // Simplificação
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        JLabel nome = new JLabel("<html><center>"+p.getNome()+"</center></html>", SwingConstants.CENTER);
        nome.setFont(new Font("Segoe UI", Font.BOLD, 14));
        center.add(icon);
        center.add(nome);
        
        // Botão Adicionar
        JButton btnAdd = new JButton("Adicionar • €" + p.getPreco());
        btnAdd.setBackground(MainView.ACCENT_COLOR);
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFocusPainted(false);
        btnAdd.addActionListener(e -> popupQuantidade(p));
        
        card.add(center, BorderLayout.CENTER);
        card.add(btnAdd, BorderLayout.SOUTH);
        
        gridProdutos.add(card);
    }
    
    // --- LÓGICA DE INTERAÇÃO ---
    
    private void popupQuantidade(Produto p) {
        String qtdStr = JOptionPane.showInputDialog(this, "Quantidade para " + p.getNome() + ":", "1");
        if(qtdStr != null && !qtdStr.isEmpty()) {
            try {
                int qtd = Integer.parseInt(qtdStr);
                if(qtd > 0) adicionarItemAoCarrinho(p, qtd);
            } catch(NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Número inválido.");
            }
        }
    }
    
    private void adicionarItemAoCarrinho(Produto p, int qtd) {
        // Lógica Visual
        double totalItem = p.getPreco().doubleValue() * qtd;
        cartModel.addRow(new Object[]{p.getNome(), qtd, String.format("€ %.2f", totalItem)});
        
        // Lógica de Negócio (Controller)
        if(vendaController != null) {
            // vendaController.adicionarItem(pedidoAtual, p, qtd); // Exemplo
        }
        atualizarTotal();
    }
    
    private void removerItemSelecionado() {
        int row = cartTable.getSelectedRow();
        if(row != -1) {
            cartModel.removeRow(row);
            // Chamar controller para remover do pedido backend
            atualizarTotal();
        } else {
            JOptionPane.showMessageDialog(this, "Selecione um item para remover.");
        }
    }
    
    private void adicionarNota() {
        String nota = JOptionPane.showInputDialog(this, "Nota para a cozinha:");
        if(nota != null && !nota.isEmpty()) {
            // vendaController.adicionarNota(pedidoAtual, nota);
            JOptionPane.showMessageDialog(this, "Nota adicionada!");
        }
    }
    
    private void cancelarPedido() {
        if(cartModel.getRowCount() > 0) {
            int opt = JOptionPane.showConfirmDialog(this, "Tem a certeza?", "Cancelar", JOptionPane.YES_NO_OPTION);
            if(opt == JOptionPane.YES_OPTION) {
                cartModel.setRowCount(0);
                pedidoAtual = new Pedido(); // Reset
                atualizarTotal();
            }
        }
    }
    
    private void finalizarPedido() {
        if(cartModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "O carrinho está vazio.");
            return;
        }
        
        // Simulação de processamento
        double total = calcularTotalVisual();
        int tempoEspera = 15 + (cartModel.getRowCount() * 2); // Algoritmo fake
        long numeroPedido = System.currentTimeMillis() % 1000;
        
        String msg = String.format("<html><body><h2>Pedido Confirmado! #%d</h2>" +
                                   "<p>Total Pago: <b>€ %.2f</b></p>" +
                                   "<p>Tempo estimado: <b>%d minutos</b></p></body></html>", 
                                   numeroPedido, total, tempoEspera);
        
        JOptionPane.showMessageDialog(this, msg, "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        
        // Limpar
        cartModel.setRowCount(0);
        pedidoAtual = new Pedido();
        atualizarTotal();
    }
    
    private void atualizarTotal() {
        totalLabel.setText(String.format("Total: € %.2f", calcularTotalVisual()));
    }
    
    private double calcularTotalVisual() {
        double total = 0;
        for(int i=0; i<cartModel.getRowCount(); i++) {
            String priceStr = (String) cartModel.getValueAt(i, 2); // "€ 10.00"
            try {
                total += Double.parseDouble(priceStr.replace("€", "").replace(",", ".").trim());
            } catch(Exception e) { /* ignore */ }
        }
        return total;
    }
    
    private JButton createBtn(String txt, Color bg) {
        JButton b = new JButton(txt);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setFocusPainted(false);
        return b;
    }
}