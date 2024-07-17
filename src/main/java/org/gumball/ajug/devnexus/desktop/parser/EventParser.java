package org.gumball.ajug.devnexus.desktop.parser;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.gumball.ajug.devnexus.desktop.config.IngestProperties;
import org.gumball.ajug.devnexus.desktop.model.Room;
import org.gumball.ajug.devnexus.desktop.model.Session;
import org.gumball.ajug.devnexus.desktop.model.SessionSpeaker;
import org.gumball.ajug.devnexus.desktop.model.Speaker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EventParser {
    private final IngestProperties ingestProperties;

    Map<Integer, Room> rooms = new HashMap<>();
    Map<String, Session> sessionMap = new HashMap<>();
    List<SessionSpeaker> sessionSpeakers = new ArrayList<>();

    DocumentContext getDocument() throws IOException {
        String data = Files.readString(Paths.get(ingestProperties.getEventsFileName()));
        return JsonPath.parse(data);
    }

    public void parse() throws IOException {
        System.err.println("Parsing events");
        DocumentContext dc = getDocument();
        List list = dc.read("[*].sessions");
        List sessions = (List)list.get(0);
        List<Session> sessionList = sessions.stream().map(this::mapToSession).toList();
        sessionMap = sessionList.stream()
                .collect(Collectors.toMap(Session::getId, Function.identity()));
        System.err.println("Done Parsing events");
    }


    Session mapToSession(Object sessionObject) {
        Session session = new Session();
        Map sessionMap = (Map) sessionObject;
        session.setId(sessionMap.get("id").toString());
        Object obj = sessionMap.get("description");
        if (Objects.nonNull(obj)) {
            session.setDescription(obj.toString());
        }
        obj = sessionMap.get("title");
        if (Objects.nonNull(obj)){
            session.setTitle(obj.toString());
        }
        Integer roomId = (Integer)sessionMap.get("roomId");
        String roomName = (String)sessionMap.get("room");
        session.setSessionRoom(getRoom(roomId, roomName));
        LocalDateTime localDate = LocalDateTime
                .from(DateTimeFormatter.ISO_LOCAL_DATE_TIME
                        .parse(sessionMap.get("startsAt").toString()));
        session.setStarts(localDate.toInstant(ZoneOffset.UTC));
        localDate = LocalDateTime
                .from(DateTimeFormatter.ISO_LOCAL_DATE_TIME
                        .parse(sessionMap.get("endsAt").toString()));
        session.setEnds(localDate.toInstant(ZoneOffset.UTC));
        System.err.println("Session: " + session);
        mapToSpeakers(session, sessionMap.get("speakers"));
        mapToCategory(session, sessionMap.get("categories"));
        return session;
    }

    Room getRoom(Integer roomId, String roomName) {
        if (rooms.containsKey(roomId)) {
            return rooms.get(roomId);
        }
        Room room = new Room();
        room.setId(roomId);
        room.setName(roomName);
        rooms.put(roomId, room);
        return room;
    }

    void mapToSpeakers(Session session, Object speakerMap) {
        List<Map<String, Object>> speakerList = (List<Map<String, Object>>) speakerMap;
        for (Map<String, Object> speaker : speakerList) {
            String speakerId = speaker.get("id").toString();
            String speakerName = speaker.get("name").toString();
            Speaker speaker1 = new Speaker(speakerId, speakerName);
            SessionSpeaker sessionSpeaker = new SessionSpeaker(session, speaker1);
            this.sessionSpeakers.add(sessionSpeaker);
        }
    }

    void mapToCategory(Session session, Object categoryListObject) {
        List<Map<String, Object>> categoryList = (List<Map<String, Object>>) categoryListObject;
        for (Map<String, Object> category : categoryList) {
            if (!category.get("name").toString().equals("Track")) {
                continue;
            }
            Object items = category.get("categoryItems");
            List<Map<String, Object>> categoryItemList = (List<Map<String, Object>>) items;
            for (Map<String, Object> categoryItem : categoryItemList) {
                String categoryName = categoryItem.get("name").toString();
                session.setTrack(categoryName);
            }
        }
    }
}
