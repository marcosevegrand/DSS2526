package dss2526.app;

import dss2526.ui.view.AppUI;

public class App {
    public static void main(String[] args) {
        try {
            new AppUI().run();
            System.out.println("Aplicação terminada.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}