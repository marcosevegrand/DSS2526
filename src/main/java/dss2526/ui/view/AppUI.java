package dss2526.ui.view;

import dss2526.ui.controller.GestaoController;
import dss2526.ui.controller.ProducaoController;
import dss2526.ui.controller.VendaController;
import dss2526.ui.util.NewMenu;

public class AppUI {
    
    public void run() {
        NewMenu mainMenu = new NewMenu("--- Sistema Restaurante ---", new String[]{
            "Gestão",
            "Venda",
            "Produção",
        });

        mainMenu.setHandler(1, () -> {
            new GestaoUI(new GestaoController()).show();
        });
        mainMenu.setHandler(2, () -> {
            new VendaUI(new VendaController()).show();
        });
        mainMenu.setHandler(3, () -> {
            new ProducaoUI(new ProducaoController()).show();
        });
    }
}