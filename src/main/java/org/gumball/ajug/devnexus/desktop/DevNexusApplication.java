package org.gumball.ajug.devnexus.desktop;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.WeakEventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.controlsfx.control.HiddenSidesPane;
import org.gumball.ajug.devnexus.desktop.controllers.Controller;
import org.gumball.ajug.devnexus.desktop.events.EventBus;
import org.gumball.ajug.devnexus.desktop.events.EventTypes;
import org.gumball.ajug.devnexus.desktop.events.LoadViewEvent;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DevNexusApplication extends Application {
    private final Map<String, Parent> VIEW_MAP = new HashMap<>();
    private ConfigurableApplicationContext springContext;
    private final ViewLoadHandler viewLoadHandler = new ViewLoadHandler();
    private final HiddenSidesPane sidePane = new HiddenSidesPane();
    private EventBus eventBus;

    @Override
    public void init() {
        springContext = new SpringApplicationBuilder(SpringBooter.class).run();
        eventBus = (EventBus) springContext.getBean("eventBus");
        eventBus.addListener(EventTypes.LOAD_VIEW, new WeakEventHandler(viewLoadHandler));
    }

    @Override
    public void stop() {
        this.springContext.close();
        Platform.exit();
    }

    @Override
    public void start(Stage stage) throws IOException {
        Parent root = loadFXML("main-view");
        VIEW_MAP.put("dashboard", root);

        Scene scene = new Scene(root, 1024, 768);
        scene.getStylesheets().add(getClass().getResource("app.css").toExternalForm());
        stage.setScene(scene);
        scene.setFill(Color.TRANSPARENT);

        stage.show();
    }

    public void setRoot(String fxml) throws IOException {
        Parent p = VIEW_MAP.get(fxml);
        if (p == null) {
            p = loadFXML(fxml);
            VIEW_MAP.put(fxml, p);
        }
        Event showing = new Event(fxml, null, EventTypes.SHOW_VIEW);
        eventBus.fireEvent(showing);
        sidePane.setContent(p);
    }

    private Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(DevNexusApplication.class.getResource(fxml + ".fxml"));
        fxmlLoader.setControllerFactory(springContext::getBean);
        Parent p = fxmlLoader.load();
        Controller controller = fxmlLoader.getController();
        controller.initialize();
        return p;
    }

    public static void main(String[] args) {
        launch();
    }

    public class ViewLoadHandler implements EventHandler<LoadViewEvent> {
        @Override
        public void handle(LoadViewEvent loadViewEvent) {
            try {
                setRoot(loadViewEvent.getViewName());
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
}