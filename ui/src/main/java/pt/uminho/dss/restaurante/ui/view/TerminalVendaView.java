package pt.uminho.dss.restaurante.ui.view;

import pt.uminho.dss.restaurante.ui.controller.VendaController;
import pt.uminho.dss.restaurante.domain.entity.Pedido;
import pt.uminho.dss.restaurante.domain.enumeration.ModoConsumo;
import pt.uminho.dss.restaurante.domain.entity.Produto;
import pt.uminho.dss.restaurante.domain.entity.Menu;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TerminalVendaView extends JPanel {
    private final VendaController vendaController;
    private Pedido pedidoAtual;
    private int idTerminal = 1, idFuncionario = 1;
    
    // Contentores de Navegação (ATRIBUTOS DE CLASSE)
    private JPanel cardsContainer; 
    private CardLayout cardLayout;
    
    // Painéis das Etapas
    private JPanel painelInicio, painelModo, painelCategorias, painelCarrinho;
    
    // Componentes UI
    private JList<String> listaItens;
    private JTable tabelaCarrinho;
    private JLabel labelTotal, labelPedidoId;
    private String categoriaAtual = "TODOS";
    
    public TerminalVendaView(VendaController vendaController) {
        this.vendaController = vendaController;
        inicializarUI();
        // Agora já não dá erro porque o cardsContainer já foi inicializado
        mostrarEtapaInicio();
    }
    
    private void inicializarUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(Color.WHITE);
        
        // HEADER FIXO
        JPanel header = criarHeader();
        add(header, BorderLayout.NORTH);
        
        // CARDS (Contentor das etapas)
        cardsContainer = new JPanel(new CardLayout()); // Inicializa o atributo da classe
        cardLayout = (CardLayout) cardsContainer.getLayout();
        
        painelInicio = criarPainelInicio();
        painelModo = criarPainelModo();
        painelCategorias = criarPainelCategorias();
        painelCarrinho = criarPainelCarrinho();
        
        cardsContainer.add(painelInicio, "INICIO");
        cardsContainer.add(painelModo, "MODO");
        cardsContainer.add(painelCategorias, "CATEGORIAS");
        cardsContainer.add(painelCarrinho, "CARRINHO");
        
        add(cardsContainer, BorderLayout.CENTER);
    }
    
    private JPanel criarHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(46, 125, 50));
        header.setPreferredSize(new Dimension(0, 80));
        
        JLabel title = new JLabel(" FASTFOOD RESTAURANTE", SwingConstants.LEFT);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        header.add(title, BorderLayout.WEST);
        
        labelPedidoId = new JLabel("Pedido: --- ", SwingConstants.RIGHT);
        labelPedidoId.setFont(new Font("Arial", Font.PLAIN, 16));
        labelPedidoId.setForeground(Color.WHITE);
        header.add(labelPedidoId, BorderLayout.EAST);
        
        return header;
    }
    
    private JPanel criarPainelInicio() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        
        JButton btnIniciar = new JButton("🚀 INICIAR NOVO PEDIDO");
        btnIniciar.setFont(new Font("Arial", Font.BOLD, 22));
        btnIniciar.setBackground(new Color(76, 175, 80));
        btnIniciar.setForeground(Color.WHITE);
        btnIniciar.setPreferredSize(new Dimension(350, 100));
        
        // CORREÇÃO: Usar cardsContainer em vez de getParent()
        btnIniciar.addActionListener(e -> cardLayout.show(cardsContainer, "MODO"));
        
        panel.add(btnIniciar);
        return panel;
    }
    
    private JPanel criarPainelModo() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        JLabel titulo = new JLabel("COMO PRETENDE CONSUMIR?", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 24));
        titulo.setBorder(BorderFactory.createEmptyBorder(30,0,30,0));
        
        JPanel botoes = new JPanel(new GridLayout(1, 2, 30, 0));
        botoes.setBorder(BorderFactory.createEmptyBorder(0, 50, 50, 50));
        botoes.setBackground(Color.WHITE);
        
        JButton btnLocal = new JButton("<html><center>🍽️<br>PARA CONSUMO LOCAL</center></html>");
        JButton btnTakeaway = new JButton("<html><center>📦<br>TAKE AWAY</center></html>");
        
        btnLocal.setFont(new Font("Arial", Font.BOLD, 20));
        btnLocal.setBackground(new Color(232, 245, 233));
        btnLocal.addActionListener(e -> iniciarPedido(ModoConsumo.LOCAL));
        
        btnTakeaway.setFont(new Font("Arial", Font.BOLD, 20));
        btnTakeaway.setBackground(new Color(255, 243, 224));
        btnTakeaway.addActionListener(e -> iniciarPedido(ModoConsumo.TAKE_AWAY));
        
        botoes.add(btnLocal);
        botoes.add(btnTakeaway);
        
        panel.add(titulo, BorderLayout.NORTH);
        panel.add(botoes, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel criarPainelCategorias() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        
        JPanel categoriasPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        String[] categorias = {"TODOS", "MENUS", "PRODUTOS", "BEBIDAS", "SOBREMESAS"};
        
        for (String cat : categorias) {
            JButton btn = new JButton(cat);
            btn.setPreferredSize(new Dimension(130, 45));
            btn.setFont(new Font("Arial", Font.BOLD, 14));
            btn.addActionListener(e -> {
                categoriaAtual = cat;
                carregarItensCategoria();
            });
            categoriasPanel.add(btn);
        }
        
        listaItens = new JList<>();
        listaItens.setFont(new Font("Arial", Font.PLAIN, 18));
        listaItens.setFixedCellHeight(45);
        
        JPanel footer = new JPanel(new BorderLayout());
        JButton btnVerCarrinho = new JButton("🛒 VER CARRINHO / FINALIZAR");
        btnVerCarrinho.setFont(new Font("Arial", Font.BOLD, 16));
        btnVerCarrinho.setBackground(new Color(33, 150, 243));
        btnVerCarrinho.setForeground(Color.WHITE);
        btnVerCarrinho.addActionListener(e -> cardLayout.show(cardsContainer, "CARRINHO"));

        JButton btnAdicionar = new JButton("➕ ADICIONAR SELECIONADO");
        btnAdicionar.setBackground(new Color(76, 175, 80));
        btnAdicionar.setForeground(Color.WHITE);
        btnAdicionar.addActionListener(e -> adicionarItemSelecionado());
        
        footer.add(btnAdicionar, BorderLayout.WEST);
        footer.add(btnVerCarrinho, BorderLayout.EAST);
        
        panel.add(categoriasPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(listaItens), BorderLayout.CENTER);
        panel.add(footer, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel criarPainelCarrinho() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        
        tabelaCarrinho = new JTable();
        tabelaCarrinho.setRowHeight(30);
        
        JPanel topo = new JPanel(new BorderLayout());
        JButton btnVoltar = new JButton("← CONTINUAR A COMPRAR");
        btnVoltar.addActionListener(e -> cardLayout.show(cardsContainer, "CATEGORIAS"));
        topo.add(btnVoltar, BorderLayout.WEST);
        
        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnRemover = new JButton("🗑️ REMOVER");
        btnRemover.addActionListener(e -> removerLinhaSelecionada());
        acoes.add(btnRemover);
        
        JPanel footer = new JPanel(new BorderLayout());
        labelTotal = new JLabel("Total: €0.00 ", SwingConstants.RIGHT);
        labelTotal.setFont(new Font("Arial", Font.BOLD, 22));
        
        JButton btnPagar = new JButton("💳 FINALIZAR E PAGAR");
        btnPagar.setPreferredSize(new Dimension(250, 60));
        btnPagar.setBackground(new Color(46, 125, 50));
        btnPagar.setForeground(Color.WHITE);
        btnPagar.setFont(new Font("Arial", Font.BOLD, 18));
        btnPagar.addActionListener(e -> finalizarPedido());
        
        footer.add(labelTotal, BorderLayout.NORTH);
        footer.add(btnPagar, BorderLayout.SOUTH);
        
        panel.add(topo, BorderLayout.NORTH);
        panel.add(new JScrollPane(tabelaCarrinho), BorderLayout.CENTER);
        panel.add(acoes, BorderLayout.EAST);
        panel.add(footer, BorderLayout.SOUTH);
        
        return panel;
    }
    
    // === LÓGICA DE NAVEGAÇÃO ===

    private void mostrarEtapaInicio() {
        // CORREÇÃO: cardsContainer em vez de getParent()
        cardLayout.show(cardsContainer, "INICIO");
        pedidoAtual = null;
        labelPedidoId.setText("Pedido: ---");
    }

    private void iniciarPedido(ModoConsumo modo) {
        pedidoAtual = vendaController.novoPedido(modo, idTerminal, idFuncionario);
        labelPedidoId.setText("Pedido: #" + pedidoAtual.getId() + " (" + modo + ")");
        categoriaAtual = "TODOS";
        cardLayout.show(cardsContainer, "CATEGORIAS");
        carregarItensCategoria();
    }
    
    private void carregarItensCategoria() {
        List<?> itens = categoriaAtual.equals("MENUS") ? 
            vendaController.listarMenus() : vendaController.listarProdutos();
        
        DefaultListModel<String> model = new DefaultListModel<>();
        for (Object item : itens) {
            if (item instanceof Produto p) {
                model.addElement(p.getNome() + " - €" + String.format("%.2f", p.getPreco()));
            } else if (item instanceof Menu m) {
                model.addElement("⭐ " + m.getNome() + " - €" + String.format("%.2f", m.getPreco()));
            }
        }
        listaItens.setModel(model);
        actualizarTabela();
    }
    
    private void adicionarItemSelecionado() {
        int index = listaItens.getSelectedIndex();
        if (index < 0 || pedidoAtual == null) {
            JOptionPane.showMessageDialog(this, "Selecione um item primeiro.");
            return;
        }

        // 1. Obter o ID do Item
        int idItem = categoriaAtual.equals("MENUS") ?
                vendaController.getMenuIdByIndex(index) :
                vendaController.getProdutoIdByIndex(index);

        // 2. Escolher Quantidade (Usando um Spinner num JOptionPane)
        JSpinner spinner = new JSpinner(new SpinnerNumberModel(1, 1, 20, 1));
        int option = JOptionPane.showConfirmDialog(this, spinner, "Quantidade:", JOptionPane.OK_CANCEL_OPTION);

        if (option != JOptionPane.OK_OPTION) return;
        int quantidade = (int) spinner.getValue();

        // 3. Personalização (Simulação de ingredientes)
        // Nota: Numa App real, os ingredientes viriam do ingredienteDAO
        String personalizacao = abrirDialogoCustomizacao();

        if (idItem != -1) {
            // Chamada ao controller (ajustar a assinatura se necessário no teu Controller)
            vendaController.adicionarItem(pedidoAtual.getId(), idItem, quantidade, personalizacao);
            actualizarTabela();
            JOptionPane.showMessageDialog(this, "Item adicionado com sucesso!");
        }
    }    

    private void removerLinhaSelecionada() {
        int linha = tabelaCarrinho.getSelectedRow();
        if (linha >= 0 && pedidoAtual != null) {
            var item = pedidoAtual.getLinhas().get(linha);
            vendaController.removerItem(pedidoAtual.getId(), item.getItemId(), 1);
            actualizarTabela();
        }
    }
    
    /**
     * Cria um diálogo simples para remover ou adicionar extras
     */
    private String abrirDialogoCustomizacao() {
        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.setBorder(new TitledBorder("Personalizar Produto"));

        JCheckBox semTomate = new JCheckBox("Remover Tomate");
        JCheckBox semCebola = new JCheckBox("Remover Cebola");
        JCheckBox extraQueijo = new JCheckBox("Extra Queijo (+€1.00)");
        JCheckBox extraCarne = new JCheckBox("Extra Carne (+€2.00)");

        panel.add(new JLabel("Selecione as alterações:"));
        panel.add(semTomate);
        panel.add(semCebola);
        panel.add(extraQueijo);
        panel.add(extraCarne);

        int res = JOptionPane.showConfirmDialog(this, panel, "Customização", JOptionPane.OK_CANCEL_OPTION);

        if (res == JOptionPane.OK_OPTION) {
            StringBuilder sb = new StringBuilder();
            if (semTomate.isSelected()) sb.append("[SEM TOMATE] ");
            if (semCebola.isSelected()) sb.append("[SEM CEBOLA] ");
            if (extraQueijo.isSelected()) sb.append("[+EXTRA QUEIJO] ");
            if (extraCarne.isSelected()) sb.append("[+EXTRA CARNE] ");
            return sb.toString();
        }
        return "";
    }

    private void finalizarPedido() {
        if (pedidoAtual != null && !pedidoAtual.getLinhas().isEmpty()) {

            // 4. Adicionar Nota Geral ao Pedido
            String notaGeral = JOptionPane.showInputDialog(this, 
                "Deseja adicionar uma nota ao pedido? (Ex: Alergias, porta de trás...)", 
                "Nota do Pedido", 
                JOptionPane.QUESTION_MESSAGE);
            
            // Atualiza a nota no controller antes de pagar
            if (notaGeral != null && !notaGeral.isEmpty()) {
                vendaController.adicionarNotaAoPedido(pedidoAtual.getId(), notaGeral);
            }

            vendaController.pagarPedido(pedidoAtual.getId());

            JOptionPane.showMessageDialog(this, 
                "✅ Pedido #" + pedidoAtual.getId() + " FINALIZADO!\n" +
                "Total: €" + String.format("%.2f", pedidoAtual.getPrecoTotal()));
            
            mostrarEtapaInicio();
        } else {
            JOptionPane.showMessageDialog(this, "O carrinho está vazio!");
        }
    }    
    private void actualizarTabela() {
        if (pedidoAtual == null) {
            labelTotal.setText("Total: €0.00");
            return;
        }
        
        pedidoAtual = vendaController.obterPedido(pedidoAtual.getId());
        
        String[] colunas = {"Qtd", "Item", "Preço Un.", "Subtotal"};
        DefaultTableModel model = new DefaultTableModel(colunas, 0);
        
        for (var linha : pedidoAtual.getLinhas()) {
            model.addRow(new Object[]{
                linha.getQuantidade(),
                linha.getDescricao(),
                String.format("€%.2f", linha.getPrecoUnitario()),
                String.format("€%.2f", linha.getSubtotal())
            });
        }
        
        tabelaCarrinho.setModel(model);
        labelTotal.setText("Total: €" + String.format("%.2f", pedidoAtual.getPrecoTotal()) + " ");
    }
}