package dss2526.ui.view;

import dss2526.ui.controller.VendaController;
import dss2526.ui.util.NewMenu;

import java.util.List;
import java.util.Scanner;
import java.util.Arrays;

public class VendaUI {
    
    private final VendaController controller;
    private final Scanner scanner;

    public VendaUI() {
        this.controller = new VendaController();
        this.scanner = new Scanner(System.in);
    }

    public void run() {
        mostrarCabecalhoPrincipal();
        
        List<String> opcoesRestaurante = controller.getListaRestaurantes();
        if (opcoesRestaurante.isEmpty()) { 
            System.out.println("⚠  Não há restaurantes disponíveis no sistema."); 
            return; 
        }

        System.out.println("\n📍 SELEÇÃO DE LOCALIZAÇÃO");
        Integer indexRestaurante = escolher("Selecione o Restaurante", opcoesRestaurante);
        if (indexRestaurante == null) return;
        controller.selecionarRestaurante(indexRestaurante);

        NewMenu menu = new NewMenu("TERMINAL DE VENDA", new String[]{ "📝 Iniciar Novo Pedido" });
        menu.setHandler(1, () -> { fluxoSessaoPedido(); return false; });
        menu.run();
    }

    /**
     * Gere o fluxo de um novo pedido.
     * Se o utilizador sair do menu de gestão sem finalizar, o pedido é automaticamente cancelado.
     */
    private void fluxoSessaoPedido() {
        separador(); System.out.println("🛒  NOVO PEDIDO"); separador();
        
        String inputParaLevar = lerString("🥡 Pedido para levar? (s/n): ").trim().toLowerCase();
        boolean paraLevar = inputParaLevar.startsWith("s");
        String alergenicosInput = lerString("⚠️  Alergénios a evitar (sep. vírgula, ENTER vazio): ");
        List<String> alergenicos = alergenicosInput.isBlank() ? List.of() : Arrays.asList(alergenicosInput.split(","));

        try { 
            controller.iniciarPedido(paraLevar, alergenicos); 
        } catch (Exception e) { 
            System.out.println("❌ Erro ao iniciar pedido: " + e.getMessage()); 
            return; 
        }

        NewMenu menuPedido = new NewMenu("GESTÃO DE PEDIDO", new String[]{ 
            "➕ Adicionar Item", 
            "➖ Remover Item", 
            "👀 Consultar Pedido", 
            "✅ Finalizar Pedido" 
        });

        // Opção 1: Adicionar
        menuPedido.setHandler(1, () -> {
            List<String> itens = controller.getItensDisponiveisLegiveis();
            if (itens.isEmpty()) { 
                System.out.println("ℹ️  Não há itens disponíveis para estes critérios."); 
                return false; 
            }
            separador(); System.out.println("📋 CATÁLOGO DISPONÍVEL");
            Integer itemIndex = escolher("Selecione o Item", itens);
            if (itemIndex != null) {
                Integer qtd = lerInt("🔢 Quantidade: ");
                if (qtd > 0) {
                    controller.adicionarItemAoPedido(itemIndex, qtd);
                    System.out.println("✨ Item adicionado com sucesso.");
                }
            }
            return false;
        });

        // Opção 2: Remover
        menuPedido.setHandler(2, () -> {
            mostrarResumoPedido();
            Integer indexLinha = lerInt("🗑️  Número da linha a remover (0 para voltar): ");
            if (indexLinha > 0) { 
                controller.removerItemDoPedido(indexLinha - 1); 
                System.out.println("🗑️  Item removido."); 
            }
            return false;
        });

        // Opção 3: Consultar
        menuPedido.setHandler(3, () -> { 
            mostrarResumoPedido(); 
            esperarEnter(); 
            return false; 
        });

        // Opção 4: Finalizar
        menuPedido.setHandler(4, () -> {
            mostrarResumoPedido();
            String confirm = lerString("💳 Confirmar e pagar? (s/n): ");
            if (confirm.equalsIgnoreCase("s")) {
                String resultado = controller.finalizarPedido();
                System.out.println("\n========================================");
                System.out.println("   PEDIDO FINALIZADO COM SUCESSO!");
                System.out.println("----------------------------------------");
                System.out.println(resultado);
                System.out.println("========================================\n");
                esperarEnter();
                return true; // SAI DO MENU DE GESTÃO (FINALIZADO)
            }
            return false;
        });

        // Executa o menu
        menuPedido.run();

        // LÓGICA DE CANCELAMENTO AUTOMÁTICO
        // Se após o run() o controller ainda tiver um pedido ativo, 
        // significa que o utilizador escolheu "Sair" (0) em vez de finalizar.
        if (controller.isPedidoAtivo()) {
            System.out.println("\n🛑 Saída detetada. O pedido em curso foi cancelado.");
            controller.cancelarPedido();
        }
    }

    private void mostrarCabecalhoPrincipal() {
        System.out.println("\n\n");
        System.out.println("#########################################");
        System.out.println("#      🍔 FASTBURGER - POS SYSTEM 🍟    #");
        System.out.println("#########################################");
    }
    private void separador() { System.out.println("-----------------------------------------"); }
    private void mostrarResumoPedido() { System.out.println(); controller.getResumoPedido().forEach(System.out::println); System.out.println(); }
    
    private Integer escolher(String titulo, List<String> opcoes) {
        System.out.println("\n>>> " + titulo + " <<<");
        for (int i = 0; i < opcoes.size(); i++) { System.out.printf("%d. %s%n", i + 1, opcoes.get(i)); }
        int escolha = lerInt("👉 Opção (0 para cancelar): ");
        if (escolha <= 0 || escolha > opcoes.size()) return null;
        return escolha - 1; 
    }
    
    private Integer lerInt(String msg) {
        while (true) {
            try { 
                System.out.print(msg); 
                String line = scanner.nextLine(); 
                if (line.trim().isEmpty()) return 0; 
                return Integer.parseInt(line.trim()); 
            } catch (NumberFormatException e) { 
                System.out.println("❌ Por favor insira um número válido."); 
            }
        }
    }
    
    private String lerString(String msg) { System.out.print(msg); return scanner.nextLine(); }
    private void esperarEnter() { System.out.println("\n(Pressione ENTER para continuar...)"); scanner.nextLine(); }
}