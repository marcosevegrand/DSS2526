package dss2526.ui.view;

import dss2526.domain.contract.Item;
import dss2526.domain.entity.LinhaPedido;
import dss2526.domain.entity.Menu;
import dss2526.domain.entity.Pedido;
import dss2526.domain.entity.Produto;
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

    // Estado da venda atual
    private Pedido pedidoAtual;

    public VendaUI(IVendaFacade vendaFacade) {
        this.vendaFacade = vendaFacade;
        this.scanner = new Scanner(System.in);
        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "PT"));
    }

    /**
     * Menu Principal de Vendas
     */
    public void show() {
        String[] opcoes = {"Iniciar Nova Venda"};
        NewMenu menu = new NewMenu("Gestão de Vendas / POS", opcoes);
        
        menu.setHandler(1, () -> iniciarVenda());
        
        menu.run();
    }

    /**
     * Inicia o ciclo de vida de um pedido
     */
    private void iniciarVenda() {
        // Passo 1: Configuração Inicial
        System.out.println("\n>> Nova Venda");
        String opcaoLevar = lerString("É para levar? (S/N): ");
        boolean paraLevar = opcaoLevar.equalsIgnoreCase("S");

        try {
            // Cria o pedido no sistema via Facade
            this.pedidoAtual = vendaFacade.criarPedido(paraLevar);
            System.out.println(">> Pedido #" + pedidoAtual.getId() + " iniciado.");
            
            // Entra no loop de gestão do pedido
            menuPedidoEmCurso();
            
        } catch (Exception e) {
            System.out.println("Erro ao iniciar pedido: " + e.getMessage());
        }
    }

    /**
     * Sub-menu de gestão do pedido ativo
     */
    private void menuPedidoEmCurso() {
        boolean pedidoAtivo = true;

        while (pedidoAtivo) {
            // Atualiza o estado do pedido a partir da facade para garantir dados frescos
            atualizarEstadoPedido();
            mostrarResumoPedido();

            String[] opcoes = {
                "Adicionar Item",
                "Remover Item",
                "Confirmar e Finalizar",
                "Cancelar Pedido"
            };
            
            NewMenu menuVenda = new NewMenu("Pedido #" + pedidoAtual.getId(), opcoes);

            // 1. Adicionar Item
            menuVenda.setHandler(1, () -> adicionarItem());

            // 2. Remover Item
            menuVenda.setHandler(2, () -> removerItem());

            // 3. Confirmar (Sai do loop se sucesso)
            menuVenda.setHandler(3, () -> {
                boolean sucesso = confirmarPedido();
                return sucesso; // Se true, sai do menuVenda e volta ao loop principal (que quebrará)
            });

            // 4. Cancelar (Sai do loop se sucesso)
            menuVenda.setHandler(4, () -> {
                boolean cancelado = cancelarPedido();
                return cancelado;
            });

            menuVenda.run();
            
            // Verifica se o pedido foi finalizado ou cancelado para sair do loop while
            pedidoAtivo = false; 
        }
    }
    
    private void atualizarEstadoPedido() {
        if (pedidoAtual != null) {
            this.pedidoAtual = vendaFacade.obterPedido(pedidoAtual.getId());
        }
    }

    // --- Ações ---

    private void adicionarItem() {
        List<Item> todosItens = vendaFacade.obterItemsDisponiveis();
        
        if (todosItens.isEmpty()) {
            System.out.println(">> Não há itens registados no sistema.");
            return;
        }

        // Mostrar itens
        System.out.println("\n--- Itens Disponíveis ---");
        System.out.printf("%-4s %-35s %-10s %s\n", "#", "Designação", "Preço", "Tipo");
        System.out.println("------------------------------------------------------------");
        
        for (int i = 0; i < todosItens.size(); i++) {
            Item item = todosItens.get(i);
            String tipo = (item instanceof Menu) ? "[MENU]" : (item instanceof Produto ? "[PROD]" : "[ITEM]");
            
            System.out.printf("%-4d %-35s %-10s %s\n", 
                (i + 1), 
                item.getNome(), 
                currencyFormat.format(item.getPreco()),
                tipo
            );
        }

        int index = lerInteiro("Selecione o item (0 para voltar): ") - 1;
        if (index >= 0 && index < todosItens.size()) {
            Item selecionado = todosItens.get(index);
            int qtd = lerInteiro("Quantidade: ");
            
            // Agora pedimos a observação, pois LinhaPedido suporta isso
            String obs = lerString("Observação para este item (Enter para vazio): ");

            if (qtd > 0) {
                try {
                    vendaFacade.adicionarItem(pedidoAtual.getId(), selecionado.getId(), qtd, obs);
                    System.out.println(">> Item adicionado com sucesso.");
                } catch (Exception e) {
                    System.out.println(">> Erro ao adicionar item: " + e.getMessage());
                }
            }
        }
    }

    private void removerItem() {
        if (pedidoAtual.getLinhasPedido().isEmpty()) {
            System.out.println(">> O pedido não tem itens.");
            return;
        }

        System.out.println("\n--- Itens no Pedido ---");
        List<LinhaPedido> linhas = pedidoAtual.getLinhasPedido();
        for (int i = 0; i < linhas.size(); i++) {
            LinhaPedido lp = linhas.get(i);
            String obsStr = (lp.getObservacao() != null && !lp.getObservacao().isEmpty()) 
                    ? " [Obs: " + lp.getObservacao() + "]" 
                    : "";
            
            System.out.printf("%d. %s (Qtd: %d)%s\n", 
                (i + 1), 
                lp.getItem().getNome(), 
                lp.getQuantidade(),
                obsStr
            );
        }

        int index = lerInteiro("Selecione a linha para remover/alterar (0 para cancelar): ") - 1;
        if (index >= 0 && index < linhas.size()) {
            LinhaPedido lp = linhas.get(index);
            System.out.println("Quantidade atual: " + lp.getQuantidade());
            int qtdRemover = lerInteiro("Quantidade a remover: ");
            
            if (qtdRemover > 0) {
                try {
                    vendaFacade.removerItem(pedidoAtual.getId(), lp.getItem().getId(), qtdRemover);
                    System.out.println(">> Atualização efetuada.");
                } catch (Exception e) {
                    System.out.println(">> Erro ao remover item: " + e.getMessage());
                }
            }
        }
    }

    private boolean confirmarPedido() {
        atualizarEstadoPedido();
        if (pedidoAtual.getLinhasPedido().isEmpty()) {
            System.out.println(">> Erro: O pedido está vazio.");
            return false;
        }

        mostrarResumoDetalhado();
        
        String conf = lerString("Confirmar pagamento e finalizar pedido? (S/N): ");
        if (conf.equalsIgnoreCase("S")) {
            try {
                vendaFacade.confirmarPedido(pedidoAtual.getId());
                System.out.println("\n=================================");
                System.out.println(" PEDIDO #" + pedidoAtual.getId() + " FINALIZADO COM SUCESSO");
                System.out.println("=================================\n");
                return true; // Sai do menu
            } catch (Exception e) {
                System.out.println(">> Erro ao finalizar: " + e.getMessage());
            }
        }
        return false;
    }

    private boolean cancelarPedido() {
        String conf = lerString("Tem a certeza que deseja CANCELAR este pedido? (S/N): ");
        if (conf.equalsIgnoreCase("S")) {
            try {
                vendaFacade.cancelarPedido(pedidoAtual.getId());
                System.out.println(">> Pedido cancelado.");
                return true; // Sai do menu
            } catch (Exception e) {
                System.out.println(">> Erro ao cancelar: " + e.getMessage());
            }
        }
        return false;
    }

    // --- Helpers de Visualização ---

    private void mostrarResumoPedido() {
        BigDecimal total = calcularTotal(pedidoAtual);
        int qtdItens = pedidoAtual.getLinhasPedido().stream().mapToInt(LinhaPedido::getQuantidade).sum();
        
        System.out.printf("\n[Pedido #%d | Itens: %d | Total: %s]\n", 
            pedidoAtual.getId(), qtdItens, currencyFormat.format(total));
    }

    private void mostrarResumoDetalhado() {
        System.out.println("\n-------------------------------------------");
        System.out.println(" RESUMO DO PEDIDO #" + pedidoAtual.getId());
        System.out.println("-------------------------------------------");
        for (LinhaPedido lp : pedidoAtual.getLinhasPedido()) {
            BigDecimal sub = lp.getItem().getPreco().multiply(BigDecimal.valueOf(lp.getQuantidade()));
            
            System.out.printf(" %-2dx %-25s %10s\n", 
                lp.getQuantidade(), 
                truncate(lp.getItem().getNome(), 25), 
                currencyFormat.format(sub));
                
            if (lp.getObservacao() != null && !lp.getObservacao().trim().isEmpty()) {
                System.out.printf("     > Obs: %s\n", lp.getObservacao());
            }
        }
        System.out.println("-------------------------------------------");
        System.out.printf(" TOTAL A PAGAR: %23s\n", currencyFormat.format(calcularTotal(pedidoAtual)));
        System.out.println("-------------------------------------------");
    }

    private BigDecimal calcularTotal(Pedido p) {
        if (p == null || p.getLinhasPedido() == null) return BigDecimal.ZERO;
        return p.getLinhasPedido().stream()
                .map(l -> l.getItem().getPreco().multiply(BigDecimal.valueOf(l.getQuantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    private String truncate(String str, int width) {
        if (str.length() > width) {
            return str.substring(0, width - 3) + "...";
        }
        return str;
    }

    // --- Inputs ---

    private int lerInteiro(String msg) {
        System.out.print(msg);
        while (!scanner.hasNextInt()) {
            scanner.next(); 
            System.out.print("Número inválido. Tente novamente: ");
        }
        int num = scanner.nextInt();
        scanner.nextLine(); 
        return num;
    }

    private String lerString(String msg) {
        System.out.print(msg);
        return scanner.nextLine();
    }
}