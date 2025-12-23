package dss2526.ui.view;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.util.Duration;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import dss2526.ui.controller.ProducaoController;

public class ProducaoView extends BorderPane {

    private FlowPane tasksContainer;

    public ProducaoView(ProducaoController controller) {
        initUI();
        startAutoRefresh();
    }

    private void initUI() {
        // Toolbar
        HBox toolbar = new HBox(15);
        toolbar.setPadding(new Insets(10));
        toolbar.setStyle("-fx-background-color: #333;");
        
        Label lblStation = new Label("Estação: GRELHA / QUENTES");
        lblStation.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #fbc02d;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label lblTime = new Label();
        Timeline clock = new Timeline(new KeyFrame(Duration.seconds(1), e -> 
            lblTime.setText(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")))));
        clock.setCycleCount(Timeline.INDEFINITE);
        clock.play();
        
        toolbar.getChildren().addAll(lblStation, spacer, lblTime);
        setTop(toolbar);

        // Container Tarefas
        tasksContainer = new FlowPane();
        tasksContainer.setHgap(20);
        tasksContainer.setVgap(20);
        tasksContainer.setPadding(new Insets(20));
        tasksContainer.setStyle("-fx-background-color: #424242;");

        ScrollPane scroll = new ScrollPane(tasksContainer);
        scroll.setFitToWidth(true);
        scroll.setFitToHeight(true);
        scroll.setStyle("-fx-background-color: #424242;");
        
        setCenter(scroll);
    }

    private void startAutoRefresh() {
        Timeline refresher = new Timeline(new KeyFrame(Duration.seconds(2), e -> refreshTasks()));
        refresher.setCycleCount(Timeline.INDEFINITE);
        refresher.play();
    }

    private void refreshTasks() {
        List<MockService.KitchenTicket> activeTickets = MockService.getTickets();
        
        if (tasksContainer.getChildren().size() != activeTickets.size()) {
            tasksContainer.getChildren().clear();
            for (MockService.KitchenTicket t : activeTickets) {
                tasksContainer.getChildren().add(createTaskCard(t));
            }
        }
    }

    private VBox createTaskCard(MockService.KitchenTicket ticket) {
        VBox card = new VBox(10);
        card.setPrefSize(280, 350);
        card.getStyleClass().add("task-card");

        // Header
        HBox header = new HBox();
        header.getStyleClass().add("task-header");
        
        Label lblId = new Label("#" + ticket.id);
        lblId.setStyle("-fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: #333;");
        
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        
        Label lblTime = new Label(ticket.time.format(DateTimeFormatter.ofPattern("HH:mm")));
        lblTime.setStyle("-fx-text-fill: #333;");
        
        header.getChildren().addAll(lblId, sp, lblTime);

        // Detalhes
        Label lblType = new Label(ticket.type);
        lblType.setStyle("-fx-font-style: italic; -fx-text-fill: #555;");

        VBox itemsBox = new VBox(5);
        VBox.setVgrow(itemsBox, Priority.ALWAYS);
        for (String item : ticket.items) {
            Label l = new Label("• " + item);
            l.setWrapText(true);
            l.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #000;");
            itemsBox.getChildren().add(l);
        }

        // Ações
        HBox actions = new HBox(10);
        Button btnDelay = new Button("Atrasado");
        btnDelay.getStyleClass().add("btn-warning");
        btnDelay.setOnAction(e -> {
            card.setStyle("-fx-effect: dropshadow(three-pass-box, red, 10, 0, 0, 0);");
        });

        Button btnDone = new Button("Concluir");
        btnDone.getStyleClass().add("btn-success");
        btnDone.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(btnDone, Priority.ALWAYS);
        btnDone.setOnAction(e -> {
            MockService.removeTicket(ticket.id);
            refreshTasks();
        });

        actions.getChildren().addAll(btnDelay, btnDone);

        card.getChildren().addAll(header, lblType, new javafx.scene.control.Separator(), itemsBox, actions);
        return card;
    }
}