package dss2526.ui.view;

import dss2526.ui.controller.VendaController;
import dss2526.ui.util.NewMenu;
import java.util.*;

public class VendaUI {
    private final VendaController controller = new VendaController();
    private final Scanner sc = new Scanner(System.in);
    
    private boolean pedidoFinalizadoComSucesso = false;

    public void run() {
        List<String> rests = controller.getRestaurantes();
        int restIdx = pick("SELECIONE O RESTAURANTE", rests);
        if (restIdx == -1) return; // Utilizador escolheu Sair
        
        controller.selecionarRestaurante(restIdx);

        NewMenu.builder("MODO VENDA")
            .addOption("Iniciar Novo Pedido", this::fluxoNovoPedido)
            .addOption("Consultar Pedidos Ativos", this::fluxoConsultarAtivos)
            .run();
    }

    private boolean fluxoNovoPedido() {
        controller.iniciarNovoPedido();
        this.pedidoFinalizadoComSucesso = false;

        // --- Filtragem de Alergénios ---
        List<String> alergNames = controller.getListaAlergenicos();
        if (!alergNames.isEmpty()) {
            System.out.println("\n--- ALERGÉNIOS DETECTADOS ---");
            for (int i = 0; i < alergNames.size(); i++) System.out.println((i + 1) + ". " + alergNames.get(i));
            System.out.print("Números a excluir (ex: 1 3) ou ENTER para nenhum: ");
            String input = sc.nextLine();
            
            if (!input.isBlank()) {
                try {
                    List<String> selecionados = Arrays.stream(input.split(" "))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .map(s -> {
                            try { return Integer.parseInt(s) - 1; } catch (NumberFormatException e) { return -1; }
                        })
                        // Proteção: Filtra apenas índices que existem na lista
                        .filter(i -> i >= 0 && i < alergNames.size()) 
                        .map(alergNames::get)
                        .toList();
                    
                    controller.definirExclusoes(selecionados);
                } catch (Exception e) { 
                    System.out.println("Erro ao processar filtros. Prosseguindo sem filtros."); 
                }
            }
        }

        // --- Menu de Gestão do Pedido ---
        NewMenu.builder("GESTÃO DO PEDIDO")
            .addOption("Adicionar Item", this::adicionarItem)
            .addOption("Remover Item", this::removerItem)
            .addOption("Finalizar Pedido", this::finalizarPedido)
            .run();
        
        // Se o menu fechou e não foi sucesso, cancela.
        if (!pedidoFinalizadoComSucesso) {
            controller.cancelar();
            System.out.println("Pedido abortado/cancelado.");
        }
        
        return false;
    }

    private boolean adicionarItem() {
        List<String> cat = controller.getCatalogo();
        if (cat.isEmpty()) {
            System.out.println("Nenhum item disponível com os filtros/stock atuais.");
            return false;
        }
        
        int idx = pick("CATÁLOGO", cat);
        if (idx == -1) return false; // Voltar

        try {
            System.out.print("Quantidade: ");
            int qtd = Integer.parseInt(sc.nextLine().trim());
            if (qtd <= 0) { 
                System.out.println("Quantidade deve ser superior a 0."); 
                return false; 
            }
            
            System.out.print("Observação: ");
            String obs = sc.nextLine();
            
            // O Controller valida se idx é válido internamente também
            if (controller.adicionarItem(idx, qtd, obs)) {
                System.out.println("Item adicionado.");
            } else {
                System.out.println("Erro ao adicionar item (Índice inválido).");
            }
        } catch (NumberFormatException e) { 
            System.out.println("Entrada inválida. Digite um número."); 
        }
        return false;
    }

    private boolean removerItem() {
        List<String> cart = controller.getLinhasCarrinho();
        if (cart.isEmpty()) { 
            System.out.println("O carrinho está vazio."); 
            return false; 
        }
        
        int idx = pick("REMOVER ITEM", cart);
        if (idx == -1) return false; // Voltar

        try {
            System.out.print("Quantidade a remover: ");
            int q = Integer.parseInt(sc.nextLine().trim());
            if (q <= 0) {
                System.out.println("Quantidade deve ser positiva.");
                return false;
            }
            controller.removerItem(idx, q);
        } catch (NumberFormatException e) { 
            System.out.println("Valor inválido."); 
        }
        return false;
    }

    private boolean finalizarPedido() {
        List<String> resumo = controller.getResumoDetalhado();
        // Verifica se só tem as linhas de rodapé (significa carrinho vazio)
        if (resumo.size() <= 2) { 
            System.out.println("(Carrinho vazio - adicione itens antes de finalizar)");
            return false;
        }
        
        System.out.println("\n===== RESUMO DO PEDIDO =====");
        resumo.forEach(System.out::println);
        
        System.out.print("\nDeseja confirmar a finalização? (S/N): ");
        if (sc.nextLine().trim().equalsIgnoreCase("s")) {
            int pay = pick("OPÇÃO DE PAGAMENTO", List.of("TERMINAL (Direto)", "CAIXA (Balcão)"));
            if (pay == -1) return false; // Voltar
            
            String mensagemFinal = controller.pagar(pay == 0 ? 1 : 2);
            System.out.println("\n" + mensagemFinal);
            
            this.pedidoFinalizadoComSucesso = true;
            return true; // Encerra o loop do menu "GESTÃO DO PEDIDO"
        }
        return false;
    }

    private boolean fluxoConsultarAtivos() {
        List<String> ativos = controller.getEcraPedidosAtivos();
        System.out.println("\n--- PEDIDOS EM PROCESSAMENTO ---");
        if (ativos.isEmpty()) System.out.println("Nenhum pedido ativo.");
        else ativos.forEach(System.out::println);
        return false;
    }

    /**
     * Método seguro para seleção em listas.
     * @return índice baseado em 0 (0..size-1) ou -1 se escolher Sair/Inválido
     */
    private int pick(String t, List<String> l) {
        System.out.println("\n--- " + t + " ---");
        for (int i = 0; i < l.size(); i++) System.out.println((i + 1) + ". " + l.get(i));
        System.out.println("0. Voltar Atrás");
        System.out.print("Seleção > ");
        try {
            String line = sc.nextLine().trim();
            if (line.isEmpty()) return -1;
            
            int ch = Integer.parseInt(line);
            
            // Lógica Crítica de Limites
            if (ch == 0) return -1; // Sair
            if (ch > 0 && ch <= l.size()) {
                return ch - 1; // Converter para índice 0-based
            }
            
            System.out.println("Opção inválida.");
            return -1;
        } catch (NumberFormatException e) { 
            System.out.println("Entrada inválida."); 
            return -1; 
        }
    }
}