package dss2526.app;

import dss2526.ui.controller.*;
import dss2526.ui.view.MainView;

import javax.swing.*;
import java.awt.*;

public class App {

    // Instanciação dos Controllers
    private final VendaController vendaController = new VendaController();
    private final ProducaoController producaoController = new ProducaoController();
    private final GestaoController GestaoController = new GestaoController();

    public static void main(String[] args) {
        // LookAndFeel Nimbus
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    UIManager.put("nimbusBase", new Color(50, 50, 50));
                    UIManager.put("nimbusBlueGrey", new Color(100, 100, 100));
                    UIManager.put("control", new Color(240, 240, 240));
                    break;
                }
            }
        } catch (Exception e) { /* Ignorar */ }

        SwingUtilities.invokeLater(() -> new App().iniciar());
    }

    public void iniciar() {
        JFrame frame = new JFrame("Sistema Restaurante DSS 25/26");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Tamanho maior pois agora é uma janela única
        frame.setSize(1280, 800);
        frame.setLocationRelativeTo(null); 

        // A App delega a UI inteira para a MainView
        MainView mainView = new MainView(vendaController, producaoController, GestaoController);
        
        frame.setContentPane(mainView);
        frame.setVisible(true);
    }
}