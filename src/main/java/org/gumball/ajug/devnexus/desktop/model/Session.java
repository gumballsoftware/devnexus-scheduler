package org.gumball.ajug.devnexus.desktop.model;

import lombok.Data;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Data
public class Session {
    private String id;

    private String title;

    private String description;

    private Instant starts;

    private Instant ends;

    private String sessionRoom;

    private String track;

    private List<String> speakers;

    public boolean isSession() {
        if (track == null)
            return false;
        return !sessionRoom.toLowerCase().contains("foyer");
    }
    public boolean isRegistration() {
        if (title == null)
            return false;
        return title.toLowerCase().contains("registration");
    }
    public String getVerbiage() {
        return String.format("%s \n%s", title, description==null?"":description);
    }
    public String getScheduledStart() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("E ha")
                .withZone(ZoneId.of("UTC"));
        return formatter.format(starts);
    }
}