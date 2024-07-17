package org.gumball.ajug.devnexus.desktop.controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import com.jayway.jsonpath.JsonPath;
import javafx.scene.control.TreeItem;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.gumball.ajug.devnexus.desktop.config.IngestProperties;
import net.minidev.json.JSONArray;
import org.springframework.stereotype.Component;


@Data
@Component
@RequiredArgsConstructor
public class MainModel {
    String title = "oof";
    private final IngestProperties ingestProperties;
    String searchText = "";
    TreeItem scheduleTreeItem;
    TreeItem speakerTreeItem;
    TreeItem eventTreeItem;
    TreeItem<String> selectedTreeItem = scheduleTreeItem;

    public void selectEventTree() {
        selectedTreeItem = eventTreeItem;
    }
    public void selectScheduleTree() {
        selectedTreeItem = scheduleTreeItem;
    }

    public void selectSpeakerTree() {
        selectedTreeItem = speakerTreeItem;
    }

    public void initialize() {
        title = "I am Devnexus ";
        try {
            scheduleTreeItem = createTree("Dates", getSchedule());
            speakerTreeItem = createTree("Speakers", getSpeakers());
            eventTreeItem = createTree("Events", getEvents());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Object getSpeakers() throws IOException {
        String data = Files.readString(Paths.get(ingestProperties.getSpeakersFileName()));
        return JsonPath.parse(data).json();
    }
    private Object getEvents() throws IOException {
        String data = Files.readString(Paths.get(ingestProperties.getEventsFileName()));
        return JsonPath.parse(data).json();
    }

    private Object getSchedule() throws IOException {
        String data = Files.readString(Paths.get(ingestProperties.getScheduleFileName()));
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
        else if (element instanceof Map.Entry<?, ?>) {
            Map.Entry<?, ?> entry = (Map.Entry<?, ?>)element;
            TreeItem<String> item = new TreeItem<>(entry.getKey()+": "+entry.getValue());
            return item;
        }

        return new TreeItem<>(name);
    }
}
