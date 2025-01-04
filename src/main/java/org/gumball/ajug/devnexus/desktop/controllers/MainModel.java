package org.gumball.ajug.devnexus.desktop.controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import com.jayway.jsonpath.JsonPath;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.gumball.ajug.devnexus.desktop.config.ApplicationProperties;
import net.minidev.json.JSONArray;
import org.gumball.ajug.devnexus.desktop.preferences.AppUserPreferences;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import static java.util.stream.Collectors.groupingBy;


@Data
@Component
@RequiredArgsConstructor
public class MainModel {
    String title = "oof";
    private final ApplicationProperties applicationProperties;
    private final AppUserPreferences appUserPreferences;
    String searchText = "";
    TreeItem scheduleTreeItem;
    TreeItem speakerTreeItem;
    TreeItem eventTreeItem;
    TreeItem<String> selectedTreeItem = scheduleTreeItem;

    ObservableList<org.gumball.ajug.devnexus.desktop.model.Session> scheduleTableList = FXCollections.observableArrayList();

    public void selectEventTree() {
        selectedTreeItem = eventTreeItem;
    }
    public void selectScheduleTree() {
        selectedTreeItem = scheduleTreeItem;
    }
    public void selectSpeakerTree() {
        selectedTreeItem = speakerTreeItem;
    }

    record Session(String startsAt, String id, String title, String description, String room) {};

    public void initialize() {
        title = "I am Devnexus ";
        try {
//            scheduleTreeItem = createTree("Dates", getSchedule());
            speakerTreeItem = createTree("Speakers", getSpeakers());
            JSONArray events = (JSONArray) getEvents();
            Map eventsMap = (Map)events.get(0);
            JSONArray sessions = (JSONArray) eventsMap.get("sessions");

            Map<String, List<Session>> sessionsGroupedByPeriod =
                    sessions.stream()
                            .map(this::createSession)
                            .collect(groupingBy(Session::startsAt));

            eventTreeItem = createTree("Events", sessionsGroupedByPeriod);
            scheduleTableList.addAll(getSessions());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Session createSession (Object obj) {
        Map<String, String> object = (Map<String, String>) obj;
        String id = (String) object.get("id");
        String title = (String) object.get("title");
        String description = (String) object.get("description");
        String startsAt = (String) object.get("startsAt");
        String room = (String) object.get("room");
        return new Session(startsAt, id, title, description, room);
    }

private List<org.gumball.ajug.devnexus.desktop.model.Session> getSessions() {
//   http://localhost:8080/schedule/sessions
    RestClient restClient = RestClient.create();
    String uri = applicationProperties.getScheduleAPIBaseURL() + "/sessions";
    List<org.gumball.ajug.devnexus.desktop.model.Session> result =
            restClient.get()
            .uri(uri)
            .retrieve()
            .body(new ParameterizedTypeReference<>() {});
    assert result != null;
    result.sort((o1, o2) -> o1.getStarts().compareTo(o2.getStarts()));
    return result;
}
    private Object getSpeakers() throws IOException {
        String pathString = appUserPreferences.getDownloadDirectory() + applicationProperties.getSpeakersFileName();
        String data = Files.readString(Paths.get(pathString));
        return JsonPath.parse(data).json();
    }

    private Object getEvents() throws IOException {
        String pathString = appUserPreferences.getDownloadDirectory() + applicationProperties.getEventsFileName();
        String data = Files.readString(Paths.get(pathString));
        return JsonPath.parse(data).json();
    }

    private Object getSchedule() throws IOException {
        String pathString = appUserPreferences.getDownloadDirectory() + applicationProperties.getScheduleFileName();
        String data = Files.readString(Paths.get(pathString));
        return JsonPath.parse(data).json();
    }

    TreeItem<String> searchTreeItem(String query) {
        return getTreeViewItem(selectedTreeItem, query);
    }

    TreeItem<String> searchNextTreeItem(String query) {
        if (selectedTreeItem == null)
            return searchTreeItem(query);

        TreeItem<String> currentTreeItem = selectedTreeItem;
        TreeItem<String> nextTreeItem = null;
        selectedTreeItem = scheduleTreeItem;
        do {
            nextTreeItem = searchTreeItem(query);
        } while ((nextTreeItem != currentTreeItem));
        selectedTreeItem = nextTreeItem;
        return searchTreeItem(query);
    }

    public static TreeItem<String> getTreeViewItem(TreeItem<String> item , String value)
    {
        if (item == null)
            return null;
        if (item.getValue().toUpperCase().contains(value.toUpperCase()))
            return  item;

        for (TreeItem<String> child : item.getChildren()){
            TreeItem<String> s=getTreeViewItem(child, value);
            if(s!=null)
                return s;

        }
        return null;
    }

    private TreeItem<String> createTree(String name, Object element) {
        return createTree(name, element, 0);
    }

    private TreeItem<String> createTree(String name, Object element, int keyIdx) {
        if (Objects.isNull(element)) {
            return new TreeItem<>(name);
        } else if (element instanceof JSONArray array) {
            TreeItem<String> item = new TreeItem<>(name);
            for (int i = 0, max = array.size(); i < max; i++) {
                LinkedHashMap linkedHashMap = (LinkedHashMap) array.get(i);
                Object value = linkedHashMap.keySet().toArray()[keyIdx];
                if (value != null) {
                    String label = (String) value;
                    Object object = linkedHashMap.get(label);
                    if (object != null)
                        label = label + ": " + object.toString();
                    TreeItem<String> child = createTree(label, array.get(i));
                    item.getChildren().add(child);
                }
                else {
                    TreeItem<String> child = createTree("i am BUG", array.get(i));
                    item.getChildren().add(child);
                }
            }
            return item;
        } else if (element instanceof HashMap) {
            TreeItem<String> item = new TreeItem<>(name);
            ((Map<?, ?>) element).forEach((key, value) -> {
                String label = key.toString();
                if (!(value instanceof Collection<?>) &&
                        !(value instanceof Map<?, ?>)) {
                    label = label + ": " + value;
                }
                TreeItem<String> child = createTree(label, value);
                item.getChildren().add(child);
            });
            return item;
        } else if (element instanceof Map map) {
            TreeItem<String> item = new TreeItem<>(name);
            LinkedHashMap object = (LinkedHashMap) element;
            object.entrySet().stream().skip(1).forEach(property -> {
                Map.Entry<String, Object> entry = (Map.Entry<String, Object>) property;
                String label = entry.getKey();
                if (!(entry.getValue() instanceof Collection<?>) &&
                        !(entry.getValue() instanceof Map<?, ?>)) {
                    label = label + ": " + entry.getValue();
                }
                TreeItem<String> child = createTree(label,  entry.getValue());
                item.getChildren().add(child);
            });
            return item;
        }
        else if (element instanceof ArrayList<?> list) {
            TreeItem<String> item = new TreeItem<>(name);
            list.forEach(o -> {
                TreeItem<String> child = createTree(o.toString(), o);
                item.getChildren().add(child);
            });
            return item;
        } else if (element instanceof Session) {
            TreeItem<String> child = new TreeItem<>("Title: " + ((Session) element).title());
            child.getChildren().add(new TreeItem<>("Room: " + ((Session) element).room()));
            String desc = ((Session) element).description();
            if (desc != null) {
                child.getChildren().add(new TreeItem<>("Description: " + desc));
            }
            return child;
        }
        else if (element instanceof Map.Entry<?, ?>) {
            Map.Entry<?, ?> entry = (Map.Entry<?, ?>)element;
            TreeItem<String> item = new TreeItem<>(entry.getKey()+": "+entry.getValue());
            return item;
        }

        return new TreeItem<>(name);
    }
}
