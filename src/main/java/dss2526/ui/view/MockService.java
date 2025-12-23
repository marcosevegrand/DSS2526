package dss2526.ui.view;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Serviço auxiliar para simular persistência de dados e comunicação
 * entre o POS e a Cozinha em tempo real para efeitos de demonstração da UI.
 */
public class MockService {

    public static class KitchenTicket {
        public int id;
        public String type; // "No Restaurante" ou "Takeaway"
        public LocalTime time;
        public List<String> items;
        public boolean isDelayed = false;

        public KitchenTicket(int id, String type, List<String> items) {
            this.id = id;
            this.type = type;
            this.items = items;
            this.time = LocalTime.now();
        }
    }

    // Lista thread-safe para ser acedida pela UI de Venda e de Produção simultaneamente
    private static final List<KitchenTicket> activeTickets = new CopyOnWriteArrayList<>();

    static {
        // Dados iniciais de exemplo
        List<String> items1 = new ArrayList<>();
        items1.add("2x Menu Big Burger");
        items1.add("1x Coca-Cola Zero");
        activeTickets.add(new KitchenTicket(101, "No Restaurante", items1));

        List<String> items2 = new ArrayList<>();
        items2.add("1x Salada César");
        activeTickets.add(new KitchenTicket(102, "Takeaway", items2));
    }

    // Adiciona um pedido vindo do POS
    public static void addKitchenTask(int id, String type, List<VendaView.LinhaPedidoDisplay> posItems) {
        List<String> simpleItems = new ArrayList<>();
        for (VendaView.LinhaPedidoDisplay p : posItems) {
            String s = p.getQtd() + "x " + p.getItem();
            if (p.getNota() != null && !p.getNota().isEmpty()) {
                s += " (" + p.getNota() + ")";
            }
            simpleItems.add(s);
        }
        activeTickets.add(new KitchenTicket(id, type, simpleItems));
    }

    public static List<KitchenTicket> getTickets() {
        return activeTickets;
    }

    public static void removeTicket(int id) {
        activeTickets.removeIf(t -> t.id == id);
    }
}