package org.gumball.ajug.devnexus.desktop.events;

import javafx.event.Event;


public class LoadViewEvent extends Event {

    private final String viewName;

    public LoadViewEvent(String arg) {
        super(EventTypes.LOAD_VIEW);
        viewName = arg;
    }

    public String getViewName() {
        return viewName;
    }
}
