package dss2526.ui.view;

import dss2526.ui.controller.*;
import dss2526.ui.delegate.NewMenu;

public class AppUI {

    private VendaUI vendaUI;
    private ProducaoUI producaoUI;
    private GestaoUI gestaoUI;

    public AppUI(VendaController venda, ProducaoController producao, GestaoController gestao) {
        this.vendaUI = new VendaUI(venda);
        this.producaoUI = new ProducaoUI(producao);
        this.gestaoUI = new GestaoUI(gestao);
    }

    public void run() {
        NewMenu menu = new NewMenu(
            "Sistema do Restaurante - DSS2526",
            new String[] {
            "Subsistema de Venda",
            "Subsistema de Produção",
            "Subsistema de Gestão",
        });
        menu.setHandler(1, () -> vendaUI.show());
        menu.setHandler(2, () -> producaoUI.run());
        menu.setHandler(3, () -> gestaoUI.run());
        menu.run();
    }
}
