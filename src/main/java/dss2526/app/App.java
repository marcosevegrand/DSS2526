package dss2526.app;

import dss2526.ui.controller.GestaoController;
import dss2526.ui.controller.ProducaoController;
import dss2526.ui.controller.VendaController;
import dss2526.ui.view.MainView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) {
        
        VendaController vendaController = new VendaController();
        ProducaoController producaoController = new ProducaoController();
        GestaoController gestaoController = new GestaoController();

        MainView mainView = new MainView(gestaoController, vendaController, producaoController);
        
        Scene scene = new Scene(mainView, 1280, 800);
        
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        primaryStage.setTitle("DSS Restaurante 2.0");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(1024);
        primaryStage.setMinHeight(768);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}