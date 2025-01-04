package org.gumball.ajug.devnexus.desktop.controllers;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.util.Duration;
import lombok.RequiredArgsConstructor;

import org.gumball.ajug.devnexus.desktop.model.Session;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoField;

@Component
@RequiredArgsConstructor
public class MainController implements Controller {

    private final MainModel model;

    @FXML
    private TreeView eventsTree;

//    @FXML
//    private TreeView scheduleTree;

    @FXML
    private TableView scheduleTable;

    @FXML
    private TreeView speakersTree;

    @FXML
    private TextField searchTextField;

    @FXML
    private Button searchBackButton;
    @FXML
    private Button searchFwdButton;

    @FXML
    private Tab eventsTab;

    @FXML
    private Tab scheduleTab;

    @FXML
    private Tab speakersTab;

    PauseTransition timer = new PauseTransition(Duration.millis(1500));

    public void initialize() {
        model.initialize();
        initializeTable();
        scheduleTable.setItems(model.getScheduleTableList());
        speakersTree.setRoot(model.getSpeakerTreeItem());
        eventsTree.setRoot(model.getEventTreeItem());
        timer.setOnFinished(
                e -> {
                    TreeItem<String> item = model.searchTreeItem(model.getSearchText());
                    if (item != null) {
                        model.setSelectedTreeItem(item);
                        expandTreeView(item);
                    }
                });
    }


    private void initializeTable() {
        TableColumn<Session, String> titleColumn = new TableColumn<>("Title");
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("verbiage"));
        titleColumn.setCellFactory(column -> new TableCell<Session, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                    setStyle("");
                } else {
                    Text text = new Text(item);
                    text.setStyle("-fx-text-alignment:justify;");
                    text.wrappingWidthProperty().bind(getTableColumn().widthProperty().subtract(35));
                    setGraphic(text);
                }
            }
        });

        TableColumn<Session, String> startColumn = new TableColumn<>("Starts");
        startColumn.setCellValueFactory(new PropertyValueFactory<>("scheduledStart"));

        TableColumn<Session, String> roomColumn = new TableColumn<>("Room");
        roomColumn.setCellValueFactory(new PropertyValueFactory<>("sessionRoom"));

        startColumn.setResizable(false);
        roomColumn.setResizable(false);
        titleColumn.setMinWidth(1000d);
        startColumn.setMinWidth(200d);
        roomColumn.setMinWidth(200d);
        scheduleTable.getColumns().add(startColumn);
        scheduleTable.getColumns().add(titleColumn);
        scheduleTable.getColumns().add(roomColumn);

        scheduleTable.setRowFactory(tv -> new TableRow<Session>() {
            @Override
            protected void updateItem(Session item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || item.getStarts() == null)
                    setStyle("");
                else {
                    if (item.isRegistration()) {
                        setStyle("-fx-background-color: #FFFFFF; -fx-font-style: oblique; -fx-font-weight: bold;");
                        return;
                    }
                    if (!item.isSession()) {
                        setStyle("-fx-background-color: #00dd00;");
                        return;
                    }
                    LocalDateTime localDateTime = LocalDateTime.ofInstant(item.getStarts(), ZoneId.systemDefault());
                    int hour = localDateTime.getHour();
                    if (hour % 2 == 0) {
                        setStyle("-fx-background-color: #FFFFFF;");
                    } else {
                        setStyle("-fx-background-color: #aaaaaa;");
                    }
                }
            }
        });
    }

    private static <T> void expandTreeView(TreeItem<T> selectedItem) {
        if (selectedItem != null) {
            expandTreeView(selectedItem.getParent());
            if (!selectedItem.isLeaf()) {
                selectedItem.setExpanded(true);
            }
        }
    }

    public void onSearchText(KeyEvent event) {
        model.searchText = searchTextField.getText();
        timer.playFromStart();
    }

    public void goForward() {
        TreeItem<String> item = model.searchNextTreeItem(model.getSearchText());
        System.err.println("Found search item: " + item);
        /*
        if (item != null) {
            model.setSelectedTreeItem(item);
            expandTreeView(item);
            scheduleTree.scrollTo(scheduleTree.getRow(item));
        }
         */
    }


    public void goBack() {
        System.err.println("************************* Back CLICK *****************************");
        model.setTitle("Back Clicked!");
    }

    public void selectEventsView() {
        System.err.println("************************* Select Events View *****************************");
        this.model.selectEventTree();
    }

    public void selectScheduleView() {
        System.err.println("************************* Select Schedule View *****************************");
        this.model.selectScheduleTree();
    }

    public void selectSpeakersView() {
        System.err.println("************************* Select Speakers View *****************************");
        this.model.selectSpeakerTree();
    }
}