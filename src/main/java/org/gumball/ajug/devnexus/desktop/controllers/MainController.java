package org.gumball.ajug.devnexus.desktop.controllers;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.util.Duration;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MainController implements Controller {

    private final MainModel model;

    @FXML
    private TreeView eventsTree;

    @FXML
    private TreeView scheduleTree;

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
        scheduleTree.setRoot(model.getScheduleTreeItem());
        speakersTree.setRoot(model.getSpeakerTreeItem());
        eventsTree.setRoot(model.getEventTreeItem());
        timer.setOnFinished(
                e -> {
                    System.err.println("timer finished, searching for " + searchTextField.getText());
                    TreeItem<String> item = model.searchTreeItem(model.getSearchText());
                    System.err.println("Found search item: " + item);
                    if (item != null) {
                        model.setSelectedTreeItem(item);
                        expandTreeView(item);
                        scheduleTree.scrollTo(scheduleTree.getRow(item));
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
        if (item != null) {
            model.setSelectedTreeItem(item);
            expandTreeView(item);
            scheduleTree.scrollTo(scheduleTree.getRow(item));
        }
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