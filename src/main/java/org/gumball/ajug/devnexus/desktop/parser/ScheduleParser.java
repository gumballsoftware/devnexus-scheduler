package org.gumball.ajug.devnexus.desktop.parser;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.gumball.ajug.devnexus.desktop.config.IngestProperties;
import org.gumball.ajug.devnexus.desktop.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


@Component
@RequiredArgsConstructor
public class ScheduleParser {
/*
date [
{
rooms[
{
id,
name,
sessions[
{
id, title, description, start/stop, speakers[], categories[], roomId, roomName,
}
]
}
]
timeslots[]
}
]
 */
    private final IngestProperties ingestProperties;

    List<ConferenceDay> conferenceDays = new ArrayList<>();
    Map<Integer, Room> rooms = new HashMap<>();
    Map<String, Session> sessions = new HashMap<>();
    List<ScheduledSession> scheduledSessions = new ArrayList<>();
    List<SessionSpeaker> sessionSpeakers = new ArrayList<>();
    Map<Integer, Timeslot> timeslots = new HashMap<>();
    ConferenceDay conferenceDay;

    public DocumentContext getDocument() throws IOException {
        String data = Files.readString(Paths.get(ingestProperties.getScheduleFileName()));
        return JsonPath.parse(data);
    }

    AtomicInteger dayidx = new AtomicInteger();

    public void parse() throws IOException {
        DocumentContext dc = getDocument();
        List list = dc.read("[*]");
        list.parallelStream().forEach(day -> {  //date
            // conference days
            String dayKey = "[" + dayidx.get() + "].timeSlots";
            String roomKey = "[" + dayidx.get() + "].rooms";
            dayidx.incrementAndGet();
            LocalDateTime localDate = LocalDateTime
                    .from(DateTimeFormatter.ISO_LOCAL_DATE_TIME
                            .parse(((LinkedHashMap) day).get("date").toString()));
            conferenceDay = new ConferenceDay();
            conferenceDay.setDate(localDate.toInstant(ZoneOffset.UTC));
            conferenceDay.setTitle(localDate.getDayOfWeek().name());
            conferenceDays.add(conferenceDay);
            List daySlotList = dc.read(dayKey); //date.timeSlots

            TimeslotComparator timeslotComparator = new TimeslotComparator();
            List<Timeslot> dsl = daySlotList.stream()
                    .map(this::mapToTimeslot)
                    .sorted(timeslotComparator)
                    .toList();
            dsl.forEach(timeslot -> {
                timeslot.setConferenceDayDate(localDate.toLocalDate());
                this.timeslots.put(timeslot.getId(), timeslot);
                //day timeslots
                // FIXME: get rooms, room sessions, room session speakers, room session categories
                ConferenceDayTimeslot conferenceDayTimeslot = new ConferenceDayTimeslot();
                conferenceDayTimeslot.setConferenceDay(conferenceDay);
                conferenceDayTimeslot.setTimeslot(timeslot);
                ConferenceDayTimeslotId conferenceDayTimeslotId = new ConferenceDayTimeslotId();
                conferenceDayTimeslotId.setConferenceDayId(conferenceDay.getId());
                conferenceDayTimeslotId.setTimeslotId(timeslot.getId());
                conferenceDayTimeslot.setId(conferenceDayTimeslotId);
                conferenceDay.getConferenceDayTimeslots().add(conferenceDayTimeslot);
            });

            List dataList = dc.read(roomKey); //date.rooms
            List<Room> roomList = dataList.stream().map(this::mapToRoom).toList();
            AtomicInteger roomidx = new AtomicInteger();
            roomList.forEach(room -> {
                // room sessions
                // FIXME: get speakers and categories per session
                String roomSessionKey = roomKey + "[" + roomidx.get() + "]" + ".sessions";
                List daySessionList = dc.read(roomSessionKey);
                List<Session> sessionList = daySessionList.stream().map(this::mapToSession).toList();
                sessionList.stream().forEach(session -> {
                    ScheduledSession scheduledSession = new ScheduledSession();
                    scheduledSession.setRoom(room);
                    scheduledSession.setSession(session);
                    scheduledSession.setConferenceDayTimeslot(getConferenceDayTimeslot(session.getStarts()));
                    scheduledSessions.add(scheduledSession);
                    session.setSessionRoom(room);
                    this.sessions.merge(session.getId(), session, Session::merge);
                });
                roomidx.incrementAndGet();
            });
        });
    }

    ConferenceDayTimeslot getConferenceDayTimeslot(Instant date) {
        ConferenceDayTimeslot retval = null;
        LocalDate localDate = LocalDate.ofInstant(date, ZoneOffset.UTC);
        Optional<ConferenceDay> day = conferenceDays.stream()
                .filter(cd -> LocalDate.ofInstant(cd.getDate(), ZoneOffset.UTC).equals(localDate))
                .findFirst();

        if (day.isPresent()) {
            LocalTime localTime = LocalTime.ofInstant(date, ZoneOffset.UTC);
            retval = conferenceDay.getConferenceDayTimeslots().stream()
                    .filter(timeslot ->
                            localTime.equals(timeslot.getTimeslot().getStarttime()) ||
                                    localTime.isAfter(timeslot.getTimeslot().getStarttime())
                    )
                    .findFirst()
                    .orElse(null);
        }
        return retval;
//        return day.getConferenceDayTimeslots().stream()
//                .filter(timeslot -> date.isAfter(ChronoLocalDate.from(timeslot.getConferenceDay().getDate())))
//                .findFirst()
//                .get();
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
        LocalDateTime localDate = LocalDateTime
                .from(DateTimeFormatter.ISO_LOCAL_DATE_TIME
                        .parse(sessionMap.get("startsAt").toString()));
        session.setStarts(localDate.toInstant(ZoneOffset.UTC));
        localDate = LocalDateTime
                .from(DateTimeFormatter.ISO_LOCAL_DATE_TIME
                        .parse(sessionMap.get("endsAt").toString()));
        session.setEnds(localDate.toInstant(ZoneOffset.UTC));
        mapToSpeakers(session, sessionMap.get("speakers"));
        mapToCategory(session, sessionMap.get("categories"));

        Integer roomId = (Integer)sessionMap.get("roomId");
        String roomName = (String)sessionMap.get("room");
        session.setSessionRoom(getRoom(roomId, roomName));
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
        for (Map<String, Object> speakerObjectMap : speakerList) {
            String speakerId = speakerObjectMap.get("id").toString();
            String speakerName = speakerObjectMap.get("name").toString();
            Speaker speaker = new Speaker(speakerId, speakerName);
            SessionSpeaker sessionSpeaker = new SessionSpeaker(session, speaker);
            session.getSessionSpeakers().add(sessionSpeaker);
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

    Room mapToRoom(Object roomObject) {
        Map roomMap = (Map) roomObject;
        Room room = new Room();
        var id = roomMap.get("id");
        room.setId((Integer) id);
        room.setName(roomMap.get("name").toString());
        return room;
    }

    Timeslot mapToTimeslot(Object timeSlotObject) {
        Map timeslotMap = (Map) timeSlotObject;
        Timeslot timeslot = new Timeslot();
        String timeOfDay = timeslotMap.get("slotStart").toString();
        LocalTime time = LocalTime.parse(timeOfDay);
        timeslot.setStarttime(time);
        timeslot.setConferenceDay(this.dayidx.get());
        timeslot.setEndtime(time.plus(1, ChronoUnit.HOURS));

        return timeslot;
    }

    public List<ConferenceDay> getDays(Conference conference) {
        conferenceDays.stream().forEach(conferenceDay -> {conferenceDay.setConference(conference);});
        conference.setDays(conferenceDays);
        return conferenceDays;
    }

    public Map getTimeslots() {
        return this.timeslots;
    }

    public List<SessionSpeaker> getSessionSpeakers() {
        return sessionSpeakers;
    }

    public List<Speaker> getSpeakers() {
        return getSessionSpeakers().stream().map(sessionSpeaker -> sessionSpeaker.getSpeaker()).toList();
    }

    public List<Room> getRooms() {
        return rooms.values().stream().toList();
    }

    public List<Session> getSessions() {
        return sessions.values().stream().toList();
    }

    public List<ScheduledSession> getScheduledSessions() {
        return scheduledSessions;
    }
}
