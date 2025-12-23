package dss2526.ui.view;

import dss2526.ui.controller.VendaController;
import dss2526.domain.entity.Pedido;
import dss2526.domain.entity.Produto;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TerminalVendaView extends JPanel {
    private final VendaController vendaController;
    private Pedido pedidoAtual;
    
    // Gestão de ecrãs (CardLayout)
    private JPanel cardsContainer; 
    private CardLayout cardLayout;
    
    // Paleta de Cores
    private final Color PRIMARY_COLOR = new Color(46, 204, 113); // Verde Esmeralda
    private final Color ACCENT_COLOR = new Color(52, 152, 219);  // Azul
    
    public TerminalVendaView(VendaController vendaController) {
        this.vendaController = vendaController;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        // --- Header Fixo ---
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(40, 40, 40));
        header.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        JLabel brand = new JLabel("🛒 POS Terminal");
        brand.setFont(new Font("Segoe UI", Font.BOLD, 22));
        brand.setForeground(Color.WHITE);
        header.add(brand, BorderLayout.WEST);
        
        add(header, BorderLayout.NORTH);

        // --- Container de Cartões ---
        cardsContainer = new JPanel(new CardLayout());
        cardLayout = (CardLayout) cardsContainer.getLayout();
        
        // Adiciona as "páginas"
        cardsContainer.add(painelInicio(), "INICIO");
        cardsContainer.add(painelSelecao(), "SELECAO");
        
        add(cardsContainer, BorderLayout.CENTER);
    }
    
    // Página inicial com botão gigante "Novo Pedido"
    private JPanel painelInicio() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Color.WHITE);
        
        JButton btnStart = criarBotaoGrande("Novo Pedido", "🍽️", PRIMARY_COLOR);
        btnStart.addActionListener(e -> {
            pedidoAtual = vendaController.novoPedido();
            cardLayout.show(cardsContainer, "SELECAO");
        });
        
        p.add(btnStart);
        return p;
    }
    
    // Página principal de venda (Grelha de produtos + Carrinho)
    private JPanel painelSelecao() {
        JPanel p = new JPanel(new BorderLayout());
        
        // --- Coluna Esquerda: Categorias e Grelha ---
        JPanel left = new JPanel(new BorderLayout());
        left.setBorder(new EmptyBorder(10,10,10,10));
        
        // Categorias
        JPanel cats = new JPanel(new GridLayout(1, 4, 10, 0));
        cats.add(new JButton("Menus"));
        cats.add(new JButton("Hambúrgueres"));
        cats.add(new JButton("Bebidas"));
        cats.add(new JButton("Sobremesas"));
        left.add(cats, BorderLayout.NORTH);
        
        // Grelha de Produtos
        JPanel gridProdutos = new JPanel(new GridLayout(3, 3, 10, 10)); // 3x3 grid
        gridProdutos.setBorder(new EmptyBorder(10,0,0,0));
        
        List<Produto> prods = vendaController.listarProdutos();
        for(Produto pr : prods) {
            JButton btn = new JButton("<html><center>" + pr.getNome() + "<br><b>€" + pr.getPreco() + "</b></center></html>");
            btn.setBackground(new Color(245, 245, 250));
            btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            btn.addActionListener(e -> adicionarAoCarrinho(pr.getNome(), pr.getPreco().doubleValue()));
            gridProdutos.add(btn);
        }
        
        left.add(gridProdutos, BorderLayout.CENTER);
        
        // --- Coluna Direita: Carrinho ---
        JPanel right = new JPanel(new BorderLayout());
        right.setPreferredSize(new Dimension(350, 0));
        right.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, Color.LIGHT_GRAY));
        
        // Tabela
        JTable table = new JTable(new DefaultTableModel(new Object[]{"Item", "€"}, 0));
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        right.add(new JScrollPane(table), BorderLayout.CENTER);
        
        // Botão Pagar
        JButton btnPagar = new JButton("PAGAR / FINALIZAR");
        btnPagar.setBackground(PRIMARY_COLOR);
        btnPagar.setForeground(Color.WHITE);
        btnPagar.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnPagar.setPreferredSize(new Dimension(0, 80));
        btnPagar.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Pagamento simulado com sucesso! Recibo impresso.");
            cardLayout.show(cardsContainer, "INICIO");
        });
        
        right.add(btnPagar, BorderLayout.SOUTH);
        
        p.add(left, BorderLayout.CENTER);
        p.add(right, BorderLayout.EAST);
        
        return p;
    }
    
    private JButton criarBotaoGrande(String texto, String icon, Color cor) {
        JButton b = new JButton("<html><center><font size=6>"+icon+"</font><br>"+texto+"</center></html>");
        b.setPreferredSize(new Dimension(250, 180));
        b.setBackground(cor);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setFont(new Font("Segoe UI", Font.BOLD, 20));
        return b;
    }
    
    private void adicionarAoCarrinho(String nome, double preco) {
        JOptionPane.showMessageDialog(this, "Produto adicionado: " + nome + "\n(Lógica visual simulada)");
    }
}