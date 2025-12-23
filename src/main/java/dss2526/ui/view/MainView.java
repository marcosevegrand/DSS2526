package dss2526.ui.view;

import dss2526.ui.controller.GestaoController;
import dss2526.ui.controller.ProducaoController;
import dss2526.ui.controller.VendaController;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

// Importante: Estende BorderPane (JavaFX), NÃO JFrame (Swing)
public class MainView extends BorderPane {

    private final GestaoController gestaoController;
    private final VendaController vendaController;
    private final ProducaoController producaoController;

    public MainView(GestaoController gestaoController, VendaController vendaController, ProducaoController producaoController) {
        this.gestaoController = gestaoController;
        this.vendaController = vendaController;
        this.producaoController = producaoController;
        
        initUI();
    }

    private void initUI() {
        // --- Header ---
        Label lblTitle = new Label("DSS Food System");
        lblTitle.getStyleClass().add("h1");
        
        HBox header = new HBox(lblTitle);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-padding: 20; -fx-background-color: #212121; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 0, 0, 0, 2);");
        setTop(header);

        // --- Menu Central (Tiles) ---
        HBox menuContainer = new HBox(30);
        menuContainer.setAlignment(Pos.CENTER);
        
        VBox tileVenda = createTile("POS / Vendas", "Registar pedidos e pagamentos", "btn-success");
        VBox tileCozinha = createTile("KDS / Produção", "Visualizar e gerir tarefas", "btn-warning");
        VBox tileGestao = createTile("Backoffice / Gestão", "Administração de menus e stock", "btn-primary");

        // Navegação
        tileVenda.setOnMouseClicked(e -> openWindow("Terminal de Vendas", new VendaView(vendaController)));
        tileCozinha.setOnMouseClicked(e -> openWindow("Terminal de Produção", new ProducaoView(producaoController)));
        tileGestao.setOnMouseClicked(e -> openWindow("Administração", new GestaoView(gestaoController)));

        menuContainer.getChildren().addAll(tileVenda, tileCozinha, tileGestao);
        setCenter(menuContainer);

        // --- Footer ---
        Label lblFooter = new Label("Versão 2.0 - JavaFX");
        lblFooter.setStyle("-fx-text-fill: #666; -fx-padding: 10;");
        HBox footer = new HBox(lblFooter);
        footer.setAlignment(Pos.CENTER);
        setBottom(footer);
    }

    private VBox createTile(String title, String subtitle, String colorClass) {
        VBox tile = new VBox(15);
        tile.getStyleClass().add("menu-tile");
        
        Region icon = new Region();
        icon.setPrefSize(50, 50);
        icon.setStyle("-fx-background-color: white; -fx-background-radius: 50; -fx-opacity: 0.2;");

        Label lblTitle = new Label(title);
        lblTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");
        
        Label lblSub = new Label(subtitle);
        lblSub.setWrapText(true);
        lblSub.setStyle("-fx-text-fill: #ddd; -fx-font-size: 12px; -fx-text-alignment: center;");
        
        Region bar = new Region();
        bar.setPrefSize(100, 5);
        bar.getStyleClass().add(colorClass);

        tile.getChildren().addAll(icon, lblTitle, bar, lblSub);
        return tile;
    }

    private void openWindow(String title, Region content) {
        Stage stage = new Stage();
        Scene scene = new Scene(content, 1200, 700);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        stage.setTitle(title);
        stage.setScene(scene);
        stage.show();
    }
}