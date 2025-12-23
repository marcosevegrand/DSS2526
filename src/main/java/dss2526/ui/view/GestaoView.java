package dss2526.ui.view;

import dss2526.ui.controller.GestaoController;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class GestaoView extends BorderPane {

    public GestaoView(GestaoController controller) {
        initUI();
    }

    private void initUI() {
        TabPane tabs = new TabPane();
        tabs.setStyle("-fx-background-color: #2b2d30;");

        tabs.getTabs().add(createTab("Produtos", createCrudPanel("Produto")));
        tabs.getTabs().add(createTab("Ingredientes", createCrudPanel("Ingrediente")));
        tabs.getTabs().add(createTab("Categorias", createCrudPanel("Categoria")));
        tabs.getTabs().add(createTab("Estatísticas", createStatsPanel()));

        setCenter(tabs);
    }

    private Tab createTab(String title, javafx.scene.Node content) {
        Tab t = new Tab(title);
        t.setContent(content);
        t.setClosable(false);
        return t;
    }

    private VBox createCrudPanel(String entityName) {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(20));

        HBox toolbar = new HBox(10);
        Button btnAdd = new Button("Adicionar " + entityName);
        btnAdd.getStyleClass().add("btn-success");
        Button btnEdit = new Button("Editar");
        btnEdit.getStyleClass().add("btn-warning");
        Button btnDel = new Button("Remover");
        btnDel.getStyleClass().add("btn-danger");
        
        toolbar.getChildren().addAll(btnAdd, btnEdit, btnDel);

        // Tabela
        TableView<MockItem> table = new TableView<>();
        TableColumn<MockItem, String> colName = new TableColumn<>("Nome");
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colName.setPrefWidth(200);
        
        TableColumn<MockItem, String> colVal = new TableColumn<>("Valor/Detalhe");
        colVal.setCellValueFactory(new PropertyValueFactory<>("detail"));
        colVal.setPrefWidth(150);

        table.getColumns().addAll(colName, colVal);
        table.setItems(FXCollections.observableArrayList(
            new MockItem(entityName + " A", "10.00"),
            new MockItem(entityName + " B", "15.50"),
            new MockItem(entityName + " C", "5.00")
        ));
        
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        panel.getChildren().addAll(toolbar, table);
        return panel;
    }

    private VBox createStatsPanel() {
        VBox panel = new VBox(20);
        panel.setPadding(new Insets(20));

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        BarChart<String, Number> bc = new BarChart<>(xAxis, yAxis);
        bc.setTitle("Vendas por Categoria (Hoje)");
        xAxis.setLabel("Categoria");
        yAxis.setLabel("Total (€)");

        XYChart.Series<String, Number> series1 = new XYChart.Series<>();
        series1.setName("2025");
        series1.getData().add(new XYChart.Data<>("Bebidas", 250));
        series1.getData().add(new XYChart.Data<>("Hambúrgueres", 1200));
        series1.getData().add(new XYChart.Data<>("Sobremesas", 300));

        bc.getData().add(series1);
        
        bc.lookup(".chart-plot-background").setStyle("-fx-background-color: transparent;");
        xAxis.lookup(".axis-label").setStyle("-fx-text-fill: white;");
        yAxis.lookup(".axis-label").setStyle("-fx-text-fill: white;");

        panel.getChildren().add(bc);
        return panel;
    }

    public static class MockItem {
        private String name;
        private String detail;
        public MockItem(String n, String d) { this.name = n; this.detail = d; }
        public String getName() { return name; }
        public String getDetail() { return detail; }
    }
}