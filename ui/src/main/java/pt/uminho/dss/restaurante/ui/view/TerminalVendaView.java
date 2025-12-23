package pt.uminho.dss.restaurante.ui.view;

import pt.uminho.dss.restaurante.venda.IVenda;
import pt.uminho.dss.restaurante.domain.entity.Pedido;
import pt.uminho.dss.restaurante.domain.entity.Produto;
import pt.uminho.dss.restaurante.domain.entity.Menu;
import pt.uminho.dss.restaurante.domain.enumeration.ModoConsumo;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TerminalVendaView extends JPanel {
    private IVenda vendaService;
    private Pedido pedidoAtual;
    private int idTerminal = 1;
    private int idFuncionario = 1;
    
    // Componentes UI
    private JList<String> listaProdutos;
    private JList<String> listaMenus;
    private JTable tabelaCarrinho;
    private JLabel labelTotal;
    private JLabel labelPedidoId;
    
    public TerminalVendaView(IVenda vendaService) {
        this.vendaService = vendaService;
        inicializarUI();
        carregarListas();
    }
    
    private void inicializarUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Painel superior: listas produtos/menus
        JPanel listasPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        
        listaProdutos = new JList<>();
        listaProdutos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listasPanel.add(criarScrollPane("Produtos", listaProdutos));
        
        listaMenus = new JList<>();
        listaMenus.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listasPanel.add(criarScrollPane("Menus", listaMenus));
        
        add(listasPanel, BorderLayout.NORTH);
        
        // Painel central: carrinho + controles
        JPanel centralPanel = new JPanel(new BorderLayout());
        
        tabelaCarrinho = new JTable();
        tabelaCarrinho.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        centralPanel.add(new JScrollPane(tabelaCarrinho), BorderLayout.CENTER);
        
        JPanel controlesCarrinho = new JPanel(new FlowLayout());
        JButton btnAdicionar = new JButton("➕ Adicionar Selecionado");
        JButton btnRemover = new JButton("➖ Remover Linha");
        controlesCarrinho.add(btnAdicionar);
        controlesCarrinho.add(btnRemover);
        centralPanel.add(controlesCarrinho, BorderLayout.SOUTH);
        
        add(centralPanel, BorderLayout.CENTER);
        
        // Painel inferior: total + ações
        JPanel footer = new JPanel(new BorderLayout());
        
        JPanel infoPanel = new JPanel(new FlowLayout());
        labelPedidoId = new JLabel("Pedido: ---");
        labelTotal = new JLabel("Total: €0.00");
        infoPanel.add(labelPedidoId);
        infoPanel.add(labelTotal);
        footer.add(infoPanel, BorderLayout.WEST);
        
        JPanel acoesPanel = new JPanel(new FlowLayout());
        JButton btnNovo = new JButton("🆕 Novo Pedido");
        JButton btnPagar = new JButton("💳 Pagar");
        JButton btnCancelar = new JButton("❌ Cancelar");
        
        acoesPanel.add(btnNovo);
        acoesPanel.add(btnPagar);
        acoesPanel.add(btnCancelar);
        footer.add(acoesPanel, BorderLayout.EAST);
        
        add(footer, BorderLayout.SOUTH);
        
        // Listeners
        btnNovo.addActionListener(e -> novoPedido());
        btnAdicionar.addActionListener(e -> adicionarSelecionado());
        btnRemover.addActionListener(e -> removerLinha());
        btnPagar.addActionListener(e -> pagarPedido());
        btnCancelar.addActionListener(e -> cancelarPedido());
    }
    
    private JScrollPane criarScrollPane(String titulo, JList<?> lista) {
        lista.setFont(new Font("Arial", Font.PLAIN, 14));
        JScrollPane scroll = new JScrollPane(lista);
        scroll.setBorder(BorderFactory.createTitledBorder(titulo));
        return scroll;
    }
    
    private void carregarListas() {
        // Produtos
        List<Produto> produtos = vendaService.listarProdutos();
        DefaultListModel<String> modelProdutos = new DefaultListModel<>();
        for (Produto p : produtos) {
            modelProdutos.addElement(p.getNome() + " - €" + String.format("%.2f", p.getPreco()));
        }
        listaProdutos.setModel(modelProdutos);
        
        // Menus
        List<Menu> menus = vendaService.listarMenus();
        DefaultListModel<String> modelMenus = new DefaultListModel<>();
        for (Menu m : menus) {
            modelMenus.addElement(m.getNome() + " - €" + String.format("%.2f", m.getPreco()));
        }
        listaMenus.setModel(modelMenus);
    }
    
    private void novoPedido() {
        pedidoAtual = vendaService.criarPedido(ModoConsumo.LOCAL, idTerminal, idFuncionario);
        labelPedidoId.setText("Pedido: #" + pedidoAtual.getId());
        actualizarTabela();
    }
    
    private void adicionarSelecionado() {
        if (pedidoAtual == null) {
            JOptionPane.showMessageDialog(this, "Crie um pedido primeiro!");
            return;
        }
        
        int idItem = -1;
        if (listaProdutos.getSelectedIndex() >= 0) {
            idItem = getIdFromLista(listaProdutos, vendaService.listarProdutos());
        } else if (listaMenus.getSelectedIndex() >= 0) {
            idItem = getIdFromLista(listaMenus, vendaService.listarMenus());
        }
        
        if (idItem != -1) {
            vendaService.adicionarItem(pedidoAtual.getId(), idItem, 1);
            actualizarTabela();
        } else {
            JOptionPane.showMessageDialog(this, "Selecione um produto ou menu!");
        }
    }
    
    private void removerLinha() {
        int linha = tabelaCarrinho.getSelectedRow();
        if (linha >= 0 && pedidoAtual != null) {
            // Remove 1 unidade da linha selecionada (usa índice da tabela como ID item por simplicidade)
            vendaService.removerItem(pedidoAtual.getId(), linha + 1, 1);
            actualizarTabela();
        }
    }
    
    private void pagarPedido() {
        if (pedidoAtual != null) {
            vendaService.pagarPedido(pedidoAtual.getId());
            JOptionPane.showMessageDialog(this, "Pedido #" + pedidoAtual.getId() + " pago!");
            pedidoAtual = null;
            labelPedidoId.setText("Pedido: ---");
            actualizarTabela();
        }
    }
    
    private void cancelarPedido() {
        if (pedidoAtual != null) {
            vendaService.cancelarPedido(pedidoAtual.getId());
            pedidoAtual = null;
            labelPedidoId.setText("Pedido: ---");
            actualizarTabela();
        }
    }
    
    private void actualizarTabela() {
        if (pedidoAtual == null) {
            labelTotal.setText("Total: €0.00");
            tabelaCarrinho.setModel(new DefaultTableModel());
            return;
        }
        
        pedidoAtual = vendaService.obterPedido(pedidoAtual.getId());
        
        String[] colunas = {"Qtd", "Item", "Preço", "Subtotal"};
        DefaultTableModel model = new DefaultTableModel(colunas, 0);
        
        for (int i = 0; i < pedidoAtual.getLinhas().size(); i++) {
            var linha = pedidoAtual.getLinhas().get(i);
            model.addRow(new Object[]{
                linha.getQuantidade(),
                linha.getDescricao(),
                String.format("€%.2f", linha.getPrecoUnitario()),
                String.format("€%.2f", linha.getSubtotal())
            });
        }
        
        tabelaCarrinho.setModel(model);
        labelTotal.setText("Total: €" + String.format("%.2f", pedidoAtual.getPrecoTotal()));
    }
    
    private int getIdFromLista(JList<String> lista, List<?> itens) {
        int index = lista.getSelectedIndex();
        if (index >= 0 && index < itens.size()) {
            if (itens.get(0) instanceof Produto) {
                return ((Produto) itens.get(index)).getId();
            } else {
                return ((Menu) itens.get(index)).getId();
            }
        }
        return -1;
    }
}
