package dss2526.app;

import dss2526.ui.view.AppUI;

public class App {
    public static void main(String[] args) {
        try {
            // Instantiate the main UI and start the application loop
            new AppUI().run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}