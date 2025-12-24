package dss2526.ui.view;

import java.util.*;

import dss2526.gestao.IGestaoFacade;
import dss2526.producao.IProducaoFacade;
import dss2526.venda.IVendaFacade;
import dss2526.ui.delegate.NewMenu;

public class AppUI {

    private VendaUI vendaUI;
    private ProducaoUI producaoUI;
    private GestaoUI gestaoUI;
    private Scanner sc;

    public AppUI(IVendaFacade vendaFacade, IProducaoFacade producaoFacade, IGestaoFacade gestaoFacade) {
        this.vendaUI = new VendaUI(vendaFacade);
        this.producaoUI = new ProducaoUI(producaoFacade);
        this.gestaoUI = new GestaoUI(gestaoFacade);
        this.sc = new Scanner(System.in);
    }

  
    public void run() {
        NewMenu menu = new NewMenu(
            "Sistema do Restaurante - DSS2526",
            new String[] {
            "Subsistema de Venda",
            "Subsistema de Produção",
            "Subsistema de Gestão",
        });
        menu.setHandler(1, () -> vendaUI.run());
        menu.setHandler(2, () -> producaoUI.run());
        menu.setHandler(3, () -> gestaoUI.run());
        menu.run();
    }
}
