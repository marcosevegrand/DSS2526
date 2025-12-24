package dss2526.ui.view;

import java.util.*;

import dss2526.gestao.IGestaoFacade;
import dss2526.ui.delegate.NewMenu;

public class GestaoUI {

    private IGestaoFacade gestaoFacade;
    private Scanner sc;

    public GestaoUI(IGestaoFacade gestaoFacade) {
        this.gestaoFacade = gestaoFacade;
        this.sc = new Scanner(System.in);
    }

    public void run() {
        NewMenu menu = new NewMenu(
            "Subsistema de Gestão",
            new String[] {
            "Exemplo de funcionalidade",
        });
        menu.setHandler(1, () -> exemplo());

        menu.run();
    }

    private void exemplo() {
        System.out.println("\nExemplo de funcionalidade do subsistema de Gestão.");
    }
}