package dss2526.ui.view;

import javafx.geometry.Orientation;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

// Ajuste os imports para as suas entidades reais
import dss2526.domain.entity.Produto;
import dss2526.domain.entity.LinhaPedido;

public class VendaView {

    private BorderPane root;

    public VendaView() {
        this.root = new BorderPane();
        inicializarUI();
    }

    private void inicializarUI() {
        // Usar SplitPane para dividir Catálogo (Esquerda) e Pedido Atual (Direita)
        SplitPane splitPane = new SplitPane();
        splitPane.setOrientation(Orientation.HORIZONTAL);
        splitPane.setDividerPositions(0.6); // 60% para catálogo, 40% para conta

        // --- Lado Esquerdo: Catálogo (Como uma lista de pesquisa) ---
        VBox catalogoPanel = new VBox();
        
        // Barra de Pesquisa
        HBox searchBar = new HBox(5);
        searchBar.setPadding(new javafx.geometry.Insets(5));
        TextField txtSearch = new TextField();
        txtSearch.setPromptText("Procurar artigo (ID ou Nome)...");
        HBox.setHgrow(txtSearch, Priority.ALWAYS);
        Button btnAdd = new Button("Adicionar ao Pedido");
        searchBar.getChildren().addAll(txtSearch, btnAdd);

        // Tabela Catálogo
        TableView<Produto> tableCatalogo = new TableView<>();
        TableColumn<Produto, String> colCatNome = new TableColumn<>("Artigo");
        colCatNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        TableColumn<Produto, Double> colCatPreco = new TableColumn<>("Preço Unit.");
        colCatPreco.setCellValueFactory(new PropertyValueFactory<>("preco"));
        tableCatalogo.getColumns().add(colCatNome);
        tableCatalogo.getColumns().add(colCatPreco);
        tableCatalogo.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(tableCatalogo, Priority.ALWAYS);

        catalogoPanel.getChildren().addAll(searchBar, tableCatalogo);

        // --- Lado Direito: Linhas do Pedido (A "Conta") ---
        VBox pedidoPanel = new VBox();
        
        Label lblPedido = new Label("Pedido Atual #N/A");
        lblPedido.setStyle("-fx-font-weight: bold; -fx-padding: 5;");
        
        TableView<LinhaPedido> tablePedido = new TableView<>();
        TableColumn<LinhaPedido, String> colPedItem = new TableColumn<>("Item");
        // Ajustar getters conforme classe LinhaPedido
        // colPedItem.setCellValueFactory(new PropertyValueFactory<>("produtoNome")); 
        
        TableColumn<LinhaPedido, Integer> colPedQtd = new TableColumn<>("Qtd");
        colPedQtd.setCellValueFactory(new PropertyValueFactory<>("quantidade"));

        TableColumn<LinhaPedido, Double> colPedTotal = new TableColumn<>("Subtotal");
        // colPedTotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));

        tablePedido.getColumns().add(colPedItem);
        tablePedido.getColumns().add(colPedQtd);
        tablePedido.getColumns().add(colPedTotal);

        tablePedido.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(tablePedido, Priority.ALWAYS);

        // Barra de Total e Ações
        ToolBar actionFooter = new ToolBar();
        Label lblTotal = new Label("Total: 0.00 €");
        lblTotal.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        Button btnFinalizar = new Button("Finalizar / Pagar");
        Button btnCancelar = new Button("Cancelar");
        
        // Espaçador
        javafx.scene.layout.Region spacer = new javafx.scene.layout.Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        actionFooter.getItems().addAll(btnCancelar, spacer, lblTotal, btnFinalizar);

        pedidoPanel.getChildren().addAll(lblPedido, tablePedido, actionFooter);

        splitPane.getItems().addAll(catalogoPanel, pedidoPanel);
        root.setCenter(splitPane);
    }

    public Parent getView() {
        return root;
    }
}