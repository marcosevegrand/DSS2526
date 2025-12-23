package dss2526.ui.view;

import dss2526.ui.controller.VendaController;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.util.Optional;
import java.util.Random;

public class VendaView extends BorderPane {

    private final VendaController controller;

    // Modelo de dados para a Tabela JavaFX
    public static class LinhaPedidoDisplay {
        private final SimpleStringProperty item = new SimpleStringProperty();
        private final SimpleStringProperty qtd = new SimpleStringProperty();
        private final SimpleStringProperty preco = new SimpleStringProperty();
        private final SimpleStringProperty nota = new SimpleStringProperty();

        public LinhaPedidoDisplay(String item, int qtd, double preco) {
            this.item.set(item);
            this.qtd.set(String.valueOf(qtd));
            this.preco.set(String.format("%.2f €", preco * qtd));
            this.nota.set("");
        }

        // Getters e Setters para PropertyValueFactory
        public String getItem() { return item.get(); }
        public String getQtd() { return qtd.get(); }
        public String getPreco() { return preco.get(); }
        public String getNota() { return nota.get(); }
        public void setNota(String n) { this.nota.set(n); }
    }

    private ObservableList<LinhaPedidoDisplay> pedidoAtual = FXCollections.observableArrayList();
    private Label lblTotal;
    private TableView<LinhaPedidoDisplay> tabela;
    private String tipoPedido = "No Restaurante";

    public VendaView(VendaController controller) {
        this.controller = controller;
        initUI();
    }

    private void initUI() {
        // --- Painel Esquerdo: Seleção de Produtos ---
        VBox leftPanel = new VBox(10);
        leftPanel.setPadding(new Insets(15));
        leftPanel.setPrefWidth(700);
        HBox.setHgrow(leftPanel, Priority.ALWAYS); // Ocupa espaço extra

        // Filtros de Categoria
        HBox categorias = new HBox(10);
        String[] cats = {"Todos", "Hambúrgueres", "Bebidas", "Sobremesas", "Extras"};
        for (String c : cats) {
            Button b = new Button(c);
            b.getStyleClass().add("button");
            b.setPrefWidth(100);
            // Lógica de filtro seria implementada aqui
            categorias.getChildren().add(b);
        }
        
        // Grid de Produtos (Scrollável)
        ScrollPane scrollGrid = new ScrollPane();
        scrollGrid.setFitToWidth(true);
        scrollGrid.setStyle("-fx-background-color: transparent;");
        
        FlowPane gridProdutos = new FlowPane();
        gridProdutos.setHgap(15);
        gridProdutos.setVgap(15);
        gridProdutos.setPadding(new Insets(10));
        
        // Simulação de produtos vindos da BD
        adicionarProdutoMock(gridProdutos, "Big Burger", 8.50, "Hambúrgueres");
        adicionarProdutoMock(gridProdutos, "Cheeseburger", 6.00, "Hambúrgueres");
        adicionarProdutoMock(gridProdutos, "Coca-Cola", 2.00, "Bebidas");
        adicionarProdutoMock(gridProdutos, "Água", 1.00, "Bebidas");
        adicionarProdutoMock(gridProdutos, "Batatas Fritas", 3.00, "Extras");
        adicionarProdutoMock(gridProdutos, "Mousse Choco", 4.50, "Sobremesas");

        scrollGrid.setContent(gridProdutos);
        VBox.setVgrow(scrollGrid, Priority.ALWAYS);

        leftPanel.getChildren().addAll(categorias, scrollGrid);


        // --- Painel Direito: Resumo do Pedido (Ticket) ---
        VBox rightPanel = new VBox(0);
        rightPanel.setPrefWidth(450);
        rightPanel.setStyle("-fx-background-color: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), -5, 0, 0, 0);");

        // Header do Ticket
        VBox ticketHeader = new VBox(10);
        ticketHeader.setPadding(new Insets(20));
        ticketHeader.setStyle("-fx-background-color: #eee;");
        
        Label lblOrderTitle = new Label("Detalhes do Pedido");
        lblOrderTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: #333;");
        
        ComboBox<String> cmbTipo = new ComboBox<>();
        cmbTipo.getItems().addAll("No Restaurante", "Takeaway");
        cmbTipo.setValue("No Restaurante");
        cmbTipo.setMaxWidth(Double.MAX_VALUE);
        cmbTipo.setOnAction(e -> tipoPedido = cmbTipo.getValue());
        
        ticketHeader.getChildren().addAll(lblOrderTitle, cmbTipo);

        // Tabela de Itens
        tabela = new TableView<>();
        tabela.setItems(pedidoAtual);
        tabela.setPlaceholder(new Label("Selecione produtos à esquerda"));
        
        TableColumn<LinhaPedidoDisplay, String> colQtd = new TableColumn<>("Qtd");
        colQtd.setCellValueFactory(data -> data.getValue().qtd);
        colQtd.setPrefWidth(50);
        colQtd.setStyle("-fx-alignment: CENTER;");

        TableColumn<LinhaPedidoDisplay, String> colItem = new TableColumn<>("Item");
        colItem.setCellValueFactory(data -> data.getValue().item);
        // Custom Cell Factory para mostrar a Nota por baixo do nome
        colItem.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    LinhaPedidoDisplay row = getTableView().getItems().get(getIndex());
                    String text = item;
                    if (row.getNota() != null && !row.getNota().isEmpty()) {
                        text += "\n(" + row.getNota() + ")";
                    }
                    setText(text);
                }
            }
        });
        colItem.setPrefWidth(220);

        TableColumn<LinhaPedidoDisplay, String> colPreco = new TableColumn<>("Total");
        colPreco.setCellValueFactory(data -> data.getValue().preco);
        colPreco.setStyle("-fx-alignment: CENTER-RIGHT;");
        
        tabela.getColumns().addAll(colQtd, colItem, colPreco);
        VBox.setVgrow(tabela, Priority.ALWAYS);

        // Ações sobre Itens (Nota, Remover)
        HBox itemActions = new HBox(10);
        itemActions.setPadding(new Insets(10));
        itemActions.setAlignment(Pos.CENTER);
        
        Button btnNota = new Button("Adicionar Nota");
        btnNota.getStyleClass().add("button");
        btnNota.setOnAction(e -> adicionarNota());
        
        Button btnRemover = new Button("Remover Item");
        btnRemover.getStyleClass().add("btn-danger");
        btnRemover.setOnAction(e -> removerItem());
        
        itemActions.getChildren().addAll(btnNota, btnRemover);

        // Rodapé (Total e Pagamento)
        VBox footer = new VBox(15);
        footer.setPadding(new Insets(20));
        footer.setStyle("-fx-border-color: #ddd; -fx-border-width: 1 0 0 0; -fx-background-color: #f9f9f9;");
        
        lblTotal = new Label("Total: 0.00 €");
        lblTotal.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #333;");
        lblTotal.setMaxWidth(Double.MAX_VALUE);
        lblTotal.setAlignment(Pos.CENTER_RIGHT);
        
        HBox payActions = new HBox(10);
        
        Button btnCancelar = new Button("Cancelar");
        btnCancelar.getStyleClass().add("btn-danger");
        btnCancelar.setPrefWidth(120);
        btnCancelar.setPrefHeight(50);
        btnCancelar.setOnAction(e -> cancelarPedido());

        Button btnPagar = new Button("CONFIRMAR & PAGAR");
        btnPagar.getStyleClass().add("btn-success");
        btnPagar.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        HBox.setHgrow(btnPagar, Priority.ALWAYS);
        btnPagar.setMaxWidth(Double.MAX_VALUE);
        btnPagar.setPrefHeight(50);
        btnPagar.setOnAction(e -> processarPagamento());

        payActions.getChildren().addAll(btnCancelar, btnPagar);
        footer.getChildren().addAll(lblTotal, payActions);

        rightPanel.getChildren().addAll(ticketHeader, tabela, itemActions, footer);

        setCenter(leftPanel);
        setRight(rightPanel);
    }

    // --- Lógica de UI ---

    private void adicionarProdutoMock(FlowPane grid, String nome, double preco, String categoria) {
        Button btn = new Button(nome + "\n" + String.format("%.2f €", preco));
        btn.setPrefSize(140, 100);
        btn.getStyleClass().add("button");
        btn.setStyle("-fx-alignment: center; -fx-text-alignment: center; -fx-font-size: 14px;");
        
        btn.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog("1");
            dialog.setTitle("Quantidade");
            dialog.setHeaderText(nome);
            dialog.setContentText("Quantidade:");
            
            Optional<String> result = dialog.showAndWait();
            result.ifPresent(qtdStr -> {
                try {
                    int qtd = Integer.parseInt(qtdStr);
                    if (qtd > 0) {
                        pedidoAtual.add(new LinhaPedidoDisplay(nome, qtd, preco));
                        atualizarTotal();
                    }
                } catch (NumberFormatException ex) {
                    alerta("Erro", "Quantidade inválida");
                }
            });
        });
        
        grid.getChildren().add(btn);
    }

    private void atualizarTotal() {
        double total = 0;
        for (LinhaPedidoDisplay l : pedidoAtual) {
            String p = l.getPreco().replace(" €", "").replace(",", ".");
            total += Double.parseDouble(p);
        }
        lblTotal.setText(String.format("Total: %.2f €", total));
    }

    private void adicionarNota() {
        LinhaPedidoDisplay selected = tabela.getSelectionModel().getSelectedItem();
        if (selected != null) {
            TextInputDialog dialog = new TextInputDialog(selected.getNota());
            dialog.setTitle("Nota do Item");
            dialog.setHeaderText("Instrução para a cozinha (ex: Sem cebola)");
            dialog.setContentText("Nota:");
            dialog.showAndWait().ifPresent(note -> {
                selected.setNota(note);
                tabela.refresh();
            });
        } else {
            alerta("Aviso", "Selecione um item primeiro.");
        }
    }

    private void removerItem() {
        int idx = tabela.getSelectionModel().getSelectedIndex();
        if (idx >= 0) {
            pedidoAtual.remove(idx);
            atualizarTotal();
        }
    }

    private void cancelarPedido() {
        if (!pedidoAtual.isEmpty()) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Cancelar");
            confirm.setHeaderText("Tem a certeza que deseja cancelar o pedido?");
            Optional<ButtonType> res = confirm.showAndWait();
            if (res.isPresent() && res.get() == ButtonType.OK) {
                pedidoAtual.clear();
                atualizarTotal();
                tipoPedido = "No Restaurante";
            }
        }
    }

    private void processarPagamento() {
        if (pedidoAtual.isEmpty()) {
            alerta("Erro", "O pedido está vazio.");
            return;
        }

        // Lógica de negócio simulada
        int numPedido = new Random().nextInt(900) + 100;
        // Algoritmo simples de tempo: 5 min base + 3 min por item
        int tempoEspera = 5 + (pedidoAtual.size() * 3); 

        // Enviar para Cozinha através do serviço partilhado
        MockService.addKitchenTask(numPedido, tipoPedido, pedidoAtual);

        // Mostrar confirmação
        Alert info = new Alert(Alert.AlertType.INFORMATION);
        info.setTitle("Pagamento Concluído");
        info.setHeaderText("Pedido #" + numPedido + " Confirmado!");
        info.setContentText("Tempo de espera estimado: " + tempoEspera + " minutos.\n\nPor favor, aguarde a chamada do seu número.");
        info.showAndWait();

        // Limpar para próximo cliente
        pedidoAtual.clear();
        atualizarTotal();
    }

    private void alerta(String titulo, String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.show();
    }
}