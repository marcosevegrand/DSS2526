package dss2526.ui.view;

import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToolBar;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

// Assumindo classes de domínio para preencher a tabela (ajuste conforme os seus nomes reais)
import dss2526.domain.entity.Ingrediente;
import dss2526.domain.entity.Produto;

public class GestaoView {

    private BorderPane root;

    public GestaoView() {
        this.root = new BorderPane();
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        // --- Aba 1: Inventário (Ingredientes) ---
        Tab tabStock = new Tab("Inventário / Stock");
        tabStock.setContent(criarTabelaStock());

        // --- Aba 2: Catálogo (Produtos/Menu) ---
        Tab tabMenu = new Tab("Catálogo / Menu");
        tabMenu.setContent(criarTabelaMenu());

        // --- Aba 3: Pessoal (Opcional) ---
        Tab tabPessoal = new Tab("Funcionários");
        // tabPessoal.setContent(...);

        tabPane.getTabs().addAll(tabStock, tabMenu, tabPessoal);
        root.setCenter(tabPane);
    }

    private VBox criarTabelaStock() {
        // Toolbar de ações simples
        Button btnAdd = new Button("+ Adicionar Registo");
        Button btnEdit = new Button("Editar Selecionado");
        Button btnDel = new Button("X Remover");
        ToolBar toolBar = new ToolBar(btnAdd, btnEdit, btnDel);

        // Tabela Estilo Base de Dados
        TableView<Ingrediente> tabela = new TableView<>();
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Ingrediente, String> colId = new TableColumn<>("ID");
        // Ajuste "nome" para o getter real da sua classe Ingrediente (ex: getNome)
        colId.setCellValueFactory(new PropertyValueFactory<>("id")); 
        
        TableColumn<Ingrediente, String> colNome = new TableColumn<>("Ingrediente");
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));

        TableColumn<Ingrediente, Double> colQtd = new TableColumn<>("Quantidade Atual");
        colQtd.setCellValueFactory(new PropertyValueFactory<>("quantidade"));

        TableColumn<Ingrediente, String> colUnidade = new TableColumn<>("Unidade");
        colUnidade.setCellValueFactory(new PropertyValueFactory<>("unidade"));

        tabela.getColumns().add(colId);
        tabela.getColumns().add(colNome);
        tabela.getColumns().add(colQtd);
        tabela.getColumns().add(colUnidade);

        // Adicionar dados mockados para visualização
        // tabela.setItems(service.getIngredientes()); 

        VBox container = new VBox(toolBar, tabela);
        container.setFillWidth(true);
        // Tabela cresce para ocupar espaço
        javafx.scene.layout.VBox.setVgrow(tabela, javafx.scene.layout.Priority.ALWAYS);
        
        return container;
    }

    private VBox criarTabelaMenu() {
        Button btnAdd = new Button("+ Novo Prato");
        ToolBar toolBar = new ToolBar(btnAdd);

        TableView<Produto> tabela = new TableView<>();
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Produto, String> colNome = new TableColumn<>("Nome do Prato");
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));

        TableColumn<Produto, Double> colPreco = new TableColumn<>("Preço (€)");
        colPreco.setCellValueFactory(new PropertyValueFactory<>("preco"));

        tabela.getColumns().add(colNome);
        tabela.getColumns().add(colPreco);

        VBox container = new VBox(toolBar, tabela);
        javafx.scene.layout.VBox.setVgrow(tabela, javafx.scene.layout.Priority.ALWAYS);
        return container;
    }

    public Parent getView() {
        return root;
    }
}