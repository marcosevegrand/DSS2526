package dss2526.ui.view;

import java.util.*;

import dss2526.venda.IVendaFacade;
import dss2526.ui.delegate.NewMenu;

public class VendaUI {

    private IVendaFacade vendaFacade;
    private Scanner sc;

    public VendaUI(IVendaFacade vendaFacade) {
        this.vendaFacade = vendaFacade;
        this.sc = new Scanner(System.in);
    }

    public void run() {
        NewMenu menu = new NewMenu(
            "Subsistema de Venda",
            new String[] {
            "Exemplo de funcionalidade",
        });
        menu.setHandler(1, () -> exemplo());

        menu.run();
    }

    private void exemplo() {
        System.out.println("\nExemplo de funcionalidade do subsistema de Venda.");
    }
}