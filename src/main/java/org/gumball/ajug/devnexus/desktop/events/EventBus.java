package org.gumball.ajug.devnexus.desktop.events;

import javafx.event.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class EventBus implements EventDispatcher {
    Map<EventType<?>, List<WeakEventHandler<Event>>> listenerMap;

    public EventBus() {
        listenerMap = new HashMap<>();
    }

    public <T extends Event> void addListener(EventType<T> clazz, WeakEventHandler<Event> listener) {
        List<WeakEventHandler<Event>> list = listenerMap.computeIfAbsent(clazz, k -> new ArrayList<>());
        list.add(listener);
    }

    public void fireEvent(Event event) {
        if (!listenerMap.containsKey(event.getEventType()))
            return;
        listenerMap.get(event.getEventType()).stream().forEach(eventHandler -> eventHandler.handle(event));
    }

    @Override
    public Event dispatchEvent(Event event, EventDispatchChain eventDispatchChain) {
        fireEvent(event);
        return event;
    }
}
