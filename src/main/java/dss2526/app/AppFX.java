package dss2526.app;

import dss2526.ui.view.MainView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * JavaFX App - Ponto de Entrada da Aplicação
 */
public class AppFX extends Application {

    @Override
    public void start(Stage stage) {
        // Inicializa a View Principal que contém a barra de navegação e as sub-views
        MainView mainView = new MainView();

        // Cria a cena com a view raiz e define uma resolução confortável para tabelas
        Scene scene = new Scene(mainView.getView(), 1280, 720);

        // Carrega o ficheiro de estilos (CSS)
        // O ficheiro styles.css deve estar na pasta src/main/resources/
        try {
            String css = getClass().getResource("/styles.css").toExternalForm();
            scene.getStylesheets().add(css);
        } catch (Exception e) {
            System.err.println("Aviso: Não foi possível carregar o styles.css. Verifique se o ficheiro está na pasta resources.");
            e.printStackTrace();
        }

        stage.setTitle("DSS FoodSystem - Gestão de Restaurante");
        stage.setScene(scene);
        
        // Opcional: Se preferir que a app abra logo maximizada
        // stage.setMaximized(true);
        
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}