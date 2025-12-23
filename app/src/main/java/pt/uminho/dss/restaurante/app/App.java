// ...existing code...
package pt.uminho.dss.restaurante.app;

import pt.uminho.dss.restaurante.persistence.contract.ProdutoDAO;
import pt.uminho.dss.restaurante.persistence.contract.MenuDAO;
import pt.uminho.dss.restaurante.persistence.contract.PedidoDAO;
import pt.uminho.dss.restaurante.persistence.contract.IngredienteDAO;
import pt.uminho.dss.restaurante.venda.IVenda;
import pt.uminho.dss.restaurante.venda.VendaFacade;
import pt.uminho.dss.restaurante.ui.view.TerminalVendaView;

import pt.uminho.dss.restaurante.persistence.impl.ProdutoDAOImpl;
import pt.uminho.dss.restaurante.persistence.impl.MenuDAOImpl;
import pt.uminho.dss.restaurante.persistence.impl.PedidoDAOImpl;
import pt.uminho.dss.restaurante.persistence.impl.IngredienteDAOImpl;

import javax.swing.*;
import java.awt.*;

/**
 * Fachada Mestre (Sistema) que garante a unicidade do Inventário.
 * Inicializa DAOs, cria fachadas e lança UI para teste.
 */
public class App {

    private final ProdutoDAO produtoDAO;
    private final MenuDAO menuDAO;
    private final PedidoDAO pedidoDAO;
    private final IngredienteDAO ingredienteDAO;
        
    private IVenda vendaFacade;

    public App() {
        // Inicializa DAOs (substitui pelos teus Impl concretos)
        this.produtoDAO = new ProdutoDAOImpl();
        this.menuDAO = new MenuDAOImpl();
        this.pedidoDAO = new PedidoDAOImpl();
        this.ingredienteDAO = new IngredienteDAOImpl();
        
        this.vendaFacade = new VendaFacade(produtoDAO, menuDAO, pedidoDAO);
    }

    /**
     * Lança UI principal para teste
     */
    public void iniciar() {
        SwingUtilities.invokeLater(() -> {
            criarJanelaPrincipal();
        });
    }

    private void criarJanelaPrincipal() {
        JFrame frame = new JFrame("FastFood Restaurante - DSS 2025/2026");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 700);
        frame.setLocationRelativeTo(null);
        
        // Painel de seleção de estação
        JPanel painelEstacoes = new JPanel(new GridLayout(2, 2, 20, 20));
        painelEstacoes.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        
        // Botão Venda (o teu TerminalVendaView)
        JButton btnVenda = new JButton("🛒 Terminal Venda");
        btnVenda.setFont(new Font("Arial", Font.BOLD, 20));
        btnVenda.setBackground(new Color(76, 175, 80));
        btnVenda.setForeground(Color.WHITE);
        btnVenda.addActionListener(e -> abrirTerminalVenda(frame));
        painelEstacoes.add(btnVenda);
        
        // Botões placeholders para outras estações
        JButton btnMontagem = new JButton("🔨 Estação Montagem");
        btnMontagem.setFont(new Font("Arial", Font.BOLD, 20));
        btnMontagem.setBackground(new Color(33, 150, 243));
        btnMontagem.setForeground(Color.WHITE);
        painelEstacoes.add(btnMontagem);
        
        JButton btnCaixa = new JButton("💰 Estação Caixa");
        btnCaixa.setFont(new Font("Arial", Font.BOLD, 20));
        btnCaixa.setBackground(new Color(255, 152, 0));
        btnCaixa.setForeground(Color.WHITE);
        painelEstacoes.add(btnCaixa);
        
        JButton btnGerencia = new JButton("📊 Dashboard Gerência");
        btnGerencia.setFont(new Font("Arial", Font.BOLD, 20));
        btnGerencia.setBackground(new Color(156, 39, 176));
        btnGerencia.setForeground(Color.WHITE);
        btnGerencia.addActionListener(e -> abrirGerencia(frame));
        painelEstacoes.add(btnGerencia);
        
        frame.add(painelEstacoes);
        frame.setVisible(true);
    }
    
    private void abrirTerminalVenda(JFrame framePrincipal) {
        framePrincipal.dispose();
        JFrame vendaFrame = new JFrame("Terminal Venda");
        vendaFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        vendaFrame.setSize(1100, 750);
        vendaFrame.setLocationRelativeTo(null);
        
        TerminalVendaView vendaPanel = new TerminalVendaView(vendaFacade);
        vendaFrame.add(vendaPanel);
        vendaFrame.setVisible(true);
    }
    
    private void abrirGerencia(JFrame framePrincipal) {
        framePrincipal.dispose();
        JFrame gerenciaFrame = new JFrame("Dashboard Gerência");
        gerenciaFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gerenciaFrame.setSize(900, 600);
        gerenciaFrame.setLocationRelativeTo(null);
        
        JPanel gerenciaPanel = new JPanel(new BorderLayout());
        gerenciaPanel.add(new JLabel("RELATÓRIOS E ESTATÍSTICAS\n(Implementar aqui)", SwingConstants.CENTER), BorderLayout.CENTER);
        gerenciaFrame.add(gerenciaPanel);
        gerenciaFrame.setVisible(true);
    }

    // MAIN PARA TESTE DIRECTO
    public static void main(String[] args) {
        // Inicializa DB se necessário (adapta à tua DBConfig)
        // DBConfig.init();
        
        App app = new App();
        app.iniciar();
        
        System.out.println("🚀 FastFood Restaurante iniciado!");
        System.out.println("📱 UI disponível. Testa Venda → Novo Pedido → Adicionar itens → Pagar");
    }
}
