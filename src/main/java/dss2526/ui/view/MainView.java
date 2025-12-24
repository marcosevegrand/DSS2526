package dss2526.ui.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

public class MainView {

    private BorderPane root;
    private VendaView vendaView;
    private GestaoView gestaoView;
    private ProducaoView producaoView;

    public MainView() {
        this.root = new BorderPane();
        this.vendaView = new VendaView();
        this.gestaoView = new GestaoView();
        this.producaoView = new ProducaoView();

        inicializarLayout();
    }

    private void inicializarLayout() {
        // --- Topo: Navegação Simples (Estilo Menu de Aplicação Desktop Antiga) ---
        HBox topBar = new HBox(10);
        topBar.setPadding(new Insets(5, 10, 5, 10));
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.getStyleClass().add("top-bar");

        Label appLabel = new Label("DSS FoodSystem");
        appLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #333; -fx-font-size: 14px;");

        // Separator vertical
        Separator sep = new Separator();
        sep.setOrientation(javafx.geometry.Orientation.VERTICAL);

        // Botões de Navegação (Simples, sem caixas)
        Button btnVendas = criarBotaoNav("Vendas", () -> root.setCenter(vendaView.getView()));
        Button btnProducao = criarBotaoNav("Cozinha/Produção", () -> root.setCenter(producaoView.getView()));
        Button btnGestao = criarBotaoNav("Backoffice/Gestão", () -> root.setCenter(gestaoView.getView()));

        topBar.getChildren().addAll(appLabel, sep, btnVendas, btnProducao, btnGestao);

        // --- Centro ---
        // Começa na Venda por defeito
        root.setTop(topBar);
        root.setCenter(vendaView.getView());
    }

    private Button criarBotaoNav(String texto, Runnable acao) {
        Button btn = new Button(texto);
        btn.getStyleClass().add("nav-button"); // Classe CSS para remover bordas/background
        btn.setOnAction(e -> acao.run());
        return btn;
    }

    public Parent getView() {
        return root;
    }
}