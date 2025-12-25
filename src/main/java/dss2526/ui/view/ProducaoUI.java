package dss2526.ui.view;

import dss2526.domain.entity.Pedido;
import dss2526.ui.controller.ProducaoController;

import java.util.List;
import java.util.Scanner;

public class ProducaoUI {
    private final ProducaoController controller;
    private final Scanner scanner;

    public ProducaoUI(ProducaoController controller) {
        this.controller = controller;
        this.scanner = new Scanner(System.in);
    }

    public void show() {
    }
}