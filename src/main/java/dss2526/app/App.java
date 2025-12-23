package dss2526.app;

import com.formdev.flatlaf.FlatLightLaf;
import dss2526.ui.controller.*;
import dss2526.ui.view.MainView;

import javax.swing.*;
import java.awt.*;

public class App {

    // Instanciação dos Controllers
    // Nota: Em uma aplicação real, estes poderiam receber DAOs ou Facades no construtor
    private final VendaController vendaController = new VendaController();
    private final ProducaoController producaoController = new ProducaoController();
    private final GestaoController gestaoController = new GestaoController();

    public static void main(String[] args) {
        // Configuração do FlatLaf para uma UI moderna
        try {
            // Inicializa o FlatLaf Light (podes usar FlatDarkLaf.setup() para tema escuro)
            FlatLightLaf.setup();
        } catch (Exception e) {
            // Fallback para o sistema caso o FlatLaf não esteja disponível ou ocorra erro
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                System.err.println("Falha ao inicializar LookAndFeel: " + ex.getMessage());
            }
        }
        
        // Definições globais de UI para melhorar a renderização de texto
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        
        // Personalização extra (opcional) - Ex: Arredondar botões globalmente
        UIManager.put("Button.arc", 12);
        UIManager.put("Component.arc", 12);

        SwingUtilities.invokeLater(() -> new App().iniciar());
    }

    public void iniciar() {
        JFrame frame = new JFrame("DSS FOOD - Sistema Integrado");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1280, 800); // Tamanho HD padrão
        frame.setLocationRelativeTo(null); // Centralizar no ecrã

        // Inicializa a View Principal passando os controladores necessários
        MainView mainView = new MainView(vendaController, producaoController, gestaoController);
        
        frame.setContentPane(mainView);
        frame.setVisible(true);
    }
}