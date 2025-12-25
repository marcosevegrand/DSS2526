package dss2526.ui.view;

import dss2526.ui.controller.GestaoController;
import dss2526.ui.controller.ProducaoController;
import dss2526.ui.controller.VendaController;

import java.util.Scanner;

public class AppUI {
    
    public void run() {
        Scanner scanner = new Scanner(System.in);
        
        while (true) {
            System.out.println("\n--- Sistema Restaurante ---");
            System.out.println("1. Gestão");
            System.out.println("2. Venda");
            System.out.println("3. Produção");
            System.out.println("0. Sair");
            
            int opcao = scanner.nextInt();
            
            if (opcao == 0) break;
            
            switch (opcao) {
                case 1:
                    // Instantiate controller and pass to view
                    new GestaoUI(new GestaoController()).show();
                    break;
                case 2:
                    new VendaUI(new VendaController()).show();
                    break;
                case 3:
                    new ProducaoUI(new ProducaoController()).show();
                    break;
                default:
                    System.out.println("Opção inválida");
            }
        }
    }
}