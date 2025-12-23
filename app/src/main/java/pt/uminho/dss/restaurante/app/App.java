package pt.uminho.dss.restaurante.app;

import pt.uminho.dss.restaurante.persistence.contract.ProdutoDAO;
import pt.uminho.dss.restaurante.persistence.contract.MenuDAO;
import pt.uminho.dss.restaurante.persistence.contract.PedidoDAO;
import pt.uminho.dss.restaurante.persistence.contract.IngredienteDAO;

import pt.uminho.dss.restaurante.venda.IVenda;
import pt.uminho.dss.restaurante.venda.VendaFacade;

import pt.uminho.dss.restaurante.ui.controller.VendaController;
import pt.uminho.dss.restaurante.ui.view.TerminalVendaView;

import pt.uminho.dss.restaurante.persistence.impl.ProdutoDAOImpl;
import pt.uminho.dss.restaurante.persistence.impl.MenuDAOImpl;
import pt.uminho.dss.restaurante.persistence.impl.PedidoDAOImpl;
import pt.uminho.dss.restaurante.persistence.impl.IngredienteDAOImpl;

import pt.uminho.dss.restaurante.producao.ProducaoFacade;
import pt.uminho.dss.restaurante.ui.controller.ProducaoController;
import pt.uminho.dss.restaurante.ui.view.TerminalProducaoView;
import pt.uminho.dss.restaurante.domain.enumeration.EstacaoTrabalho;

import pt.uminho.dss.restaurante.estatistica.EstatisticaFacade;
import pt.uminho.dss.restaurante.ui.controller.EstatisticaController;
import pt.uminho.dss.restaurante.ui.view.DashboardGestaoView;

import javax.swing.*;
import java.awt.*;

/**
 * Fachada Mestre (Sistema) que garante a unicidade do Inventário.
 * Inicializa DAOs, cria fachadas, controllers e lança UIs para teste.
 */
public class App {

    private final ProdutoDAO produtoDAO;
    private final MenuDAO menuDAO;
    private final PedidoDAO pedidoDAO;
    private final IngredienteDAO ingredienteDAO;

    private IVenda vendaFacade;
    private VendaController vendaController;

    // Produção (singletons para UI)
    private ProducaoFacade producaoFacade;
    private ProducaoController producaoController;

    // Estatísticas
    private EstatisticaFacade estatisticaFacade;
    private EstatisticaController estatisticaController;

    public App() {
        // Inicializa DAOs (substitui pelos teus Impl concretos)
        this.produtoDAO = new ProdutoDAOImpl();
        this.menuDAO = new MenuDAOImpl();
        this.pedidoDAO = new PedidoDAOImpl();
        this.ingredienteDAO = new IngredienteDAOImpl();

        // Facade / controllers
        this.vendaFacade = new VendaFacade(produtoDAO, menuDAO, pedidoDAO);
        this.vendaController = new VendaController(vendaFacade);

        this.producaoFacade = new ProducaoFacade();
        this.producaoController = new ProducaoController(producaoFacade);

        this.estatisticaFacade = new EstatisticaFacade(pedidoDAO);
        this.estatisticaController = new EstatisticaController(estatisticaFacade);
    }

    /**
     * Lança UI principal para teste
     */
    public void iniciar() {
        SwingUtilities.invokeLater(this::criarJanelaPrincipal);
    }

    private void criarJanelaPrincipal() {
        JFrame frame = new JFrame("FastFood Restaurante - DSS 2025/2026");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 700);
        frame.setLocationRelativeTo(null);

        // Painel com as três opções principais: Venda | Estações | Estatísticas
        JPanel painelOpcoes = new JPanel(new GridLayout(1, 3, 20, 20));
        painelOpcoes.setBorder(BorderFactory.createEmptyBorder(80, 80, 80, 80));

        // Botão Venda (Terminal Venda)
        JButton btnVenda = new JButton("🛒 Terminal Venda");
        btnVenda.setFont(new Font("Arial", Font.BOLD, 20));
        btnVenda.setBackground(new Color(76, 175, 80));
        btnVenda.setForeground(Color.WHITE);
        btnVenda.addActionListener(e -> abrirTerminalVenda(frame));
        painelOpcoes.add(btnVenda);

        // Botão Estaçoes de Trabalho
        JButton btnEstacoes = new JButton("🔧 Estações de Trabalho");
        btnEstacoes.setFont(new Font("Arial", Font.BOLD, 20));
        btnEstacoes.setBackground(new Color(33, 150, 243));
        btnEstacoes.setForeground(Color.WHITE);
        btnEstacoes.addActionListener(e -> abrirEstacoes(frame));
        painelOpcoes.add(btnEstacoes);

        // Botão Estatísticas
        JButton btnEstatisticas = new JButton("📊 Estatísticas");
        btnEstatisticas.setFont(new Font("Arial", Font.BOLD, 20));
        btnEstatisticas.setBackground(new Color(156, 39, 176));
        btnEstatisticas.setForeground(Color.WHITE);
        btnEstatisticas.addActionListener(e -> abrirEstatisticas(frame));
        painelOpcoes.add(btnEstatisticas);

        frame.add(painelOpcoes);
        frame.setVisible(true);
    }

    private void abrirTerminalVenda(JFrame framePrincipal) {
        framePrincipal.dispose();
        JFrame vendaFrame = new JFrame("Terminal Venda");
        vendaFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        vendaFrame.setSize(1100, 750);
        vendaFrame.setLocationRelativeTo(null);

        TerminalVendaView vendaPanel = new TerminalVendaView(vendaController);
        vendaFrame.add(vendaPanel);
        vendaFrame.setVisible(true);
    }

    private void abrirEstacoes(JFrame framePrincipal) {
        framePrincipal.dispose();
        JFrame estFrame = new JFrame("Estações de Trabalho");
        estFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        estFrame.setSize(600, 400);
        estFrame.setLocationRelativeTo(null);

        JPanel p = new JPanel(new BorderLayout(8,8));
        JLabel title = new JLabel("Escolha a Estação de Trabalho", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 16));
        p.add(title, BorderLayout.NORTH);

        JPanel listaPanel = new JPanel(new GridLayout(0,1,6,6));
        for (EstacaoTrabalho et : EstacaoTrabalho.values()) {
            JButton b = new JButton(et.toString());
            b.addActionListener(ev -> abrirTerminalProducao(estFrame, et));
            listaPanel.add(b);
        }
        p.add(new JScrollPane(listaPanel), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnVoltar = new JButton("Voltar");
        btnVoltar.addActionListener(e -> {
            estFrame.dispose();
            iniciar();
        });
        bottom.add(btnVoltar);
        p.add(bottom, BorderLayout.SOUTH);

        estFrame.add(p);
        estFrame.setVisible(true);
    }

    private void abrirTerminalProducao(JFrame framePrincipal, EstacaoTrabalho estacao) {
        framePrincipal.dispose();
        JFrame prodFrame = new JFrame("Terminal Produção - " + estacao);
        prodFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        prodFrame.setSize(900, 600);
        prodFrame.setLocationRelativeTo(null);

        TerminalProducaoView prodPanel = new TerminalProducaoView(producaoController, estacao);
        prodFrame.add(prodPanel);
        prodFrame.setVisible(true);
    }

    private void abrirEstatisticas(JFrame framePrincipal) {
        framePrincipal.dispose();
        JFrame estatFrame = new JFrame("Dashboard Estatísticas");
        estatFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        estatFrame.setSize(1000, 700);
        estatFrame.setLocationRelativeTo(null);

        DashboardGestaoView dash = new DashboardGestaoView(estatisticaController);
        estatFrame.add(dash);
        estatFrame.setVisible(true);
    }

    // MAIN PARA TESTE DIRECTO
    public static void main(String[] args) {
        App app = new App();
        app.iniciar();

        System.out.println("🚀 FastFood Restaurante iniciado!");
    }
}