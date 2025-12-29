package dss2526.ui.view;

import dss2526.ui.controller.VendaController;
import dss2526.ui.util.NewMenu;
import java.util.*;

public class VendaUI {
    private final VendaController c = new VendaController();
    private final Scanner sc = new Scanner(System.in);

    public void run() {
        c.setRestaurante(pick("SELECIONE O SEU RESTAURANTE", c.getRestaurantes()));

        List<String> alers = c.getAlergenicos();
        System.out.println("\n--- FILTRO DE ALERGÉNIOS ---");
        for(int i=0; i<alers.size(); i++) System.out.println((i+1)+". "+alers.get(i));
        System.out.print("IDs a excluir (ex: 1 3) ou ENTER para continuar: ");
        String ln = sc.nextLine();
        if(!ln.isEmpty()) c.definirExclusoes(Arrays.stream(ln.split(" ")).map(s->Integer.parseInt(s)-1).toList());

        c.iniciarNovoPedido();
        NewMenu.builder("QUIOSQUE - NOVO PEDIDO")
            .style(NewMenu.MenuStyle.ARROW)
            .addOption("Adicionar ao Carrinho", () -> {
                int it = pick("O NOSSO MENU", c.getCatalogo());
                System.out.print("Qtd: "); int q = Integer.parseInt(sc.nextLine());
                c.adicionarItem(it, q); return false;
            })
            .addOption("Gerir Carrinho (Remover)", () -> {
                List<String> cart = c.getCarrinho();
                if(!cart.isEmpty()) c.removerItem(pick("REMOVER DO CARRINHO", cart));
                return false;
            })
            .addOption("Estado dos Meus Pedidos", () -> { c.getEstadoPedidos().forEach(System.out::println); return false; })
            .addOption("FINALIZAR E PAGAR", () -> {
                System.out.println("\n" + c.getResumo());
                System.out.print("Deseja concluir o pedido? (s/n): ");
                if(sc.nextLine().equalsIgnoreCase("s")) {
                    int p = pick("OPÇÃO DE PAGAMENTO", List.of("TERMINAL (Cartão/MBWay)", "BALCÃO (Dinheiro/Caixa)"));
                    c.pagar(p + 1); 
                    System.out.println(p == 0 ? "Pagamento confirmado! O seu pedido está a ser preparado." : "Pedido registado! Por favor, efetue o pagamento no balcão.");
                    return true;
                }
                return false;
            })
            .addOption("Abortar Pedido", () -> { c.cancelar(); return true; })
            .run();
    }

    private int pick(String t, List<String> l) {
        System.out.println("\n--- " + t + " ---");
        for(int i=0; i<l.size(); i++) System.out.println((i+1)+". "+l.get(i));
        System.out.print("Opção > ");
        try { return Integer.parseInt(sc.nextLine())-1; } catch(Exception e) { return 0; }
    }
}