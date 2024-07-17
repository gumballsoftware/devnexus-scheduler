package org.gumball.ajug.devnexus.desktop.events;

import javafx.event.EventType;
import javafx.stage.WindowEvent;

public class EventTypes {
    public static final EventType<LoadViewEvent> LOAD_VIEW = new EventType<>("LOAD_VIEW");
    public static final EventType<WindowEvent> SHOW_VIEW = new EventType<>("SHOW_VIEW");
}
