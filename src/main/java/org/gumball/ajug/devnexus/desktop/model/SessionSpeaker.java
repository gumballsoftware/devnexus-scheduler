package org.gumball.ajug.devnexus.desktop.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Entity
@Table(name = "session_speaker", schema = "public")
public class SessionSpeaker {

    public SessionSpeaker() {
    }

    @EmbeddedId
    private SessionSpeakerId id;

    @MapsId("speakerId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "speaker_id", nullable = false)
    private Speaker speaker;

    @MapsId("sessionId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_id", nullable = false)
    private Session session;

    public SessionSpeaker(Session session, Speaker speaker) {
        this.id = new SessionSpeakerId();
        this.id.setSessionId(session.getId());
        this.id.setSpeakerId(speaker.getId());
        this.speaker = speaker;
        this.session = session;
    }
}