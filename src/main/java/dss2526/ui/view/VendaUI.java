package dss2526.ui.view;

import dss2526.domain.contract.Item;
import dss2526.domain.entity.LinhaPedido;
import dss2526.domain.entity.Pedido;
import dss2526.ui.delegate.NewMenu;
import dss2526.venda.IVendaFacade;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class VendaUI {

    private final IVendaFacade vendaFacade;
    private final Scanner scanner;
    private final NumberFormat currencyFormat;
    private Pedido pedidoAtual;
    
    // Precisamos guardar o restaurante atual para criar pedidos
    private int restauranteIdAtual = -1;

    public VendaUI(IVendaFacade vendaFacade) {
        this.vendaFacade = vendaFacade;
        this.scanner = new Scanner(System.in);
        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "PT"));
    }

    public void show() {
        // Pedir o ID do restaurante logo no início
        if (restauranteIdAtual == -1) {
            System.out.println("\n=== Login Terminal de Vendas ===");
            this.restauranteIdAtual = lerInteiro("ID do Restaurante: ");
        }

        String[] opcoes = {"Iniciar Nova Venda"};
        NewMenu menu = new NewMenu("Gestão de Vendas / POS", opcoes);
        menu.setHandler(1, this::iniciarVenda);
        menu.run();
    }

    private void iniciarVenda() {
        System.out.println("\n>> Nova Venda");
        String opcaoLevar = lerString("É para levar? (S/N): ");
        boolean paraLevar = opcaoLevar.equalsIgnoreCase("S");

        try {
            this.pedidoAtual = vendaFacade.criarPedido(restauranteIdAtual, paraLevar);
            System.out.println(">> Pedido #" + pedidoAtual.getId() + " iniciado.");
            menuPedidoEmCurso();
        } catch (Exception e) {
            System.out.println("Erro ao iniciar pedido: " + e.getMessage());
        }
    }

    private void menuPedidoEmCurso() {
        boolean[] continuar = {true}; 
        while (continuar[0]) {
            atualizarEstadoPedido();
            mostrarResumoPedido();

            String[] opcoes = {"Adicionar Item", "Remover Item", "Confirmar e Finalizar", "Cancelar Pedido"};
            NewMenu menuVenda = new NewMenu("Pedido #" + pedidoAtual.getId(), opcoes);
            
            menuVenda.setHandler(1, this::adicionarItem);
            menuVenda.setHandler(2, this::removerItem);
            menuVenda.setHandler(3, () -> { if (confirmarPedido()) continuar[0] = false; });
            menuVenda.setHandler(4, () -> { if (cancelarPedido()) continuar[0] = false; });

            menuVenda.run();
            if (!continuar[0]) break;
        }
    }
    
    private void atualizarEstadoPedido() {
        if (pedidoAtual != null) {
            this.pedidoAtual = vendaFacade.obterPedido(pedidoAtual.getId());
        }
    }

    private void adicionarItem() {
        List<Item> todosItens = vendaFacade.obterItemsDisponiveis();
        if (todosItens.isEmpty()) return;

        System.out.println("\n--- Itens Disponíveis ---");
        for (int i = 0; i < todosItens.size(); i++) {
            Item item = todosItens.get(i);
            System.out.printf("%d. %s (%s)\n", (i+1), item.getNome(), currencyFormat.format(item.getPreco()));
        }

        int index = lerInteiro("Item (0 para voltar): ") - 1;
        if (index >= 0 && index < todosItens.size()) {
            Item selecionado = todosItens.get(index);
            int qtd = lerInteiro("Quantidade: ");
            String obs = lerString("Observação: ");
            try {
                vendaFacade.adicionarItem(pedidoAtual.getId(), selecionado.getId(), qtd, obs);
            } catch (Exception e) { System.out.println("Erro: " + e.getMessage()); }
        }
    }

    private void removerItem() {
        List<LinhaPedido> linhas = pedidoAtual.getLinhasPedido(); 
        if (linhas.isEmpty()) return;
        for (int i = 0; i < linhas.size(); i++) {
            System.out.printf("%d. %s\n", (i+1), linhas.get(i).getItem().getNome());
        }
        int index = lerInteiro("Remover linha: ") - 1;
        if (index >= 0 && index < linhas.size()) {
            try {
                vendaFacade.removerItem(pedidoAtual.getId(), linhas.get(index).getItem().getId(), 1);
            } catch (Exception e) { System.out.println("Erro: " + e.getMessage()); }
        }
    }

    private boolean confirmarPedido() {
        if (pedidoAtual.getLinhasPedido().isEmpty()) return false;
        mostrarResumoDetalhado();
        if (lerString("Confirmar? (S/N): ").equalsIgnoreCase("S")) {
            try {
                vendaFacade.confirmarPedido(pedidoAtual.getId());
                return true;
            } catch (Exception e) { System.out.println("Erro: " + e.getMessage()); }
        }
        return false;
    }

    private boolean cancelarPedido() {
        if (lerString("Cancelar? (S/N): ").equalsIgnoreCase("S")) {
            try {
                vendaFacade.cancelarPedido(pedidoAtual.getId());
                return true;
            } catch (Exception e) { System.out.println("Erro: " + e.getMessage()); }
        }
        return false;
    }

    private void mostrarResumoPedido() {
        BigDecimal total = calcularTotal(pedidoAtual);
        System.out.printf("\n[Pedido #%d | Total: %s]\n", pedidoAtual.getId(), currencyFormat.format(total));
    }

    private void mostrarResumoDetalhado() {
        System.out.println("\n--- RESUMO ---");
        for (LinhaPedido lp : pedidoAtual.getLinhasPedido()) {
            // CORREÇÃO 2: Convertendo double para BigDecimal antes de multiplicar
            BigDecimal preco = BigDecimal.valueOf(lp.getItem().getPreco());
            BigDecimal subtotal = preco.multiply(BigDecimal.valueOf(lp.getQuantidade()));
            System.out.printf("%dx %s - %s\n", lp.getQuantidade(), lp.getItem().getNome(), currencyFormat.format(subtotal));
        }
        System.out.println("TOTAL: " + currencyFormat.format(calcularTotal(pedidoAtual)));
    }

    private BigDecimal calcularTotal(Pedido p) {
        if (p == null || p.getLinhasPedido() == null) return BigDecimal.ZERO;
        // CORREÇÃO 3: Convertendo explicitamente para BigDecimal no Stream para evitar erro de reduce
        return p.getLinhasPedido().stream()
                .map(l -> BigDecimal.valueOf(l.getItem().getPreco()).multiply(BigDecimal.valueOf(l.getQuantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private String lerString(String msg) { System.out.print(msg); return scanner.nextLine(); }
    private int lerInteiro(String msg) {
        System.out.print(msg);
        while (!scanner.hasNextInt()) { scanner.next(); }
        int n = scanner.nextInt(); scanner.nextLine(); return n;
    }
}