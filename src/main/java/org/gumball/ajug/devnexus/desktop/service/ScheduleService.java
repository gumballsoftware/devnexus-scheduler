package org.gumball.ajug.devnexus.desktop.service;

import org.gumball.ajug.devnexus.desktop.repository.*;
import lombok.RequiredArgsConstructor;
import org.gumball.ajug.devnexus.desktop.model.Conference;
import org.gumball.ajug.devnexus.desktop.model.Speaker;
import org.gumball.ajug.devnexus.desktop.parser.ScheduleParser;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final ConferenceRepository conferenceRepository;
    private final SpeakerRepository speakerRepository;
    private final RoomRepository roomRepository;
    private final SessionRepository sessionRepository;
    private final ScheduledSessionRepository scheduledSessionRepository;
    private final ScheduleParser parser;

    public void parse() throws IOException {
        System.err.println(System.currentTimeMillis() + " XXXXXXXXXX Parsing...");
        parser.parse();
        System.err.println(System.currentTimeMillis() + " Done Parsing...");
    }

    public void save() {
        Conference conference = new Conference();
        conference.setTitle("DevNexus 2024");
        Example<Conference> example = Example.of(conference);
        List<Conference> all2024Conferences = (List<Conference>) conferenceRepository.findAll(example);
        if (all2024Conferences.size() < 1) {
            conference.setDescription("Join the <dev/>olution");
            conference.setCreated(Instant.now());
            parser.getDays(conference);
            conferenceRepository.save(conference);
        }
        List<Speaker> speakers = parser.getSpeakers();
        speakerRepository.saveAll(speakers);
        roomRepository.saveAll(parser.getRooms());

        parser.getSessions().stream().forEach(session -> {
            sessionRepository.save(session);
        });

        parser.getScheduledSessions().stream().forEach(scheduledSession -> {
            scheduledSessionRepository.save(scheduledSession);
        });
    }

    public List<Conference> getAllConferences() {
        return conferenceRepository.findAll();
    }
}