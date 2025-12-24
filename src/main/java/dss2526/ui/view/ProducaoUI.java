package dss2526.ui.view;

import java.util.*;

import dss2526.producao.IProducaoFacade;
import dss2526.ui.delegate.NewMenu;

public class ProducaoUI {

    private IProducaoFacade producaoFacade;
    private Scanner sc;

    public ProducaoUI(IProducaoFacade producaoFacade) {
        this.producaoFacade = producaoFacade;
        this.sc = new Scanner(System.in);
    }

    public void run() {
        NewMenu menu = new NewMenu(
            "Subsistema de Produção",
            new String[] {
            "Exemplo de funcionalidade",
        });
        menu.setHandler(1, () -> exemplo());

        menu.run();
    }

    private void exemplo() {
        System.out.println("\nExemplo de funcionalidade do subsistema de Produção.");
    }
}