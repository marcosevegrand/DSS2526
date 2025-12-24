package dss2526.ui.view;

import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToolBar;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

// Ajuste os imports para as suas entidades reais
import dss2526.domain.entity.Pedido;

public class ProducaoView {

    private BorderPane root;

    public ProducaoView() {
        this.root = new BorderPane();
        inicializarUI();
    }

    private void inicializarUI() {
        // Topo: Filtros simples
        ToolBar filters = new ToolBar();
        filters.getItems().addAll(
            new Label("Filtros: "),
            new Button("Tudo"),
            new Button("Pendentes"),
            new Button("Em Preparação")
        );

        // Centro: Tabela de Ordens de Produção
        TableView<Pedido> tableOrdens = new TableView<>();
        tableOrdens.setPlaceholder(new Label("Sem pedidos em fila."));
        tableOrdens.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Pedido, String> colID = new TableColumn<>("Nº Pedido");
        colID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colID.setMinWidth(80);
        colID.setMaxWidth(100);

        TableColumn<Pedido, String> colMesa = new TableColumn<>("Mesa / Cliente");
        // Ajuste conforme sua entidade Pedido
        // colMesa.setCellValueFactory(new PropertyValueFactory<>("mesa")); 

        TableColumn<Pedido, String> colEstado = new TableColumn<>("Estado Atual");
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        TableColumn<Pedido, String> colHora = new TableColumn<>("Hora Entrada");
        // colHora.setCellValueFactory(new PropertyValueFactory<>("dataHora"));

        // Coluna de Ações (Botão dentro da tabela) pode ser complexo em JavaFX puro, 
        // simplificando para botões na toolbar que agem sobre a linha selecionada.
        tableOrdens.getColumns().add(colID);
        tableOrdens.getColumns().add(colHora);
        tableOrdens.getColumns().add(colMesa);
        tableOrdens.getColumns().add(colEstado);

        // Rodapé: Ações sobre o pedido selecionado
        ToolBar actions = new ToolBar();
        Button btnIniciar = new Button("Iniciar Preparação");
        Button btnConcluir = new Button("Concluir / Pronto");
        actions.getItems().addAll(new Label("Ação no Selecionado: "), btnIniciar, btnConcluir);

        VBox centerLayout = new VBox(tableOrdens, actions);
        VBox.setVgrow(tableOrdens, javafx.scene.layout.Priority.ALWAYS);

        root.setTop(filters);
        root.setCenter(centerLayout);
    }

    public Parent getView() {
        return root;
    }
}