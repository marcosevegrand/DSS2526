package dss2526.ui.view;

import dss2526.ui.util.NewMenu;

public class AppUI {
    
    public void run() {
        NewMenu mainMenu = new NewMenu("--- Sistema Restaurante DSS 25/26 ---", java.util.List.of(
            "Módulo Venda",
            "Módulo Produção",
            "Módulo Gestão"
        ));

        mainMenu.setHandler(1, () -> { new VendaUI().run(); return false; });
        mainMenu.setHandler(2, () -> { new ProducaoUI().run(); return false; });
        mainMenu.setHandler(3, () -> { new GestaoUI().run(); return false; });

        mainMenu.run();
    }
}