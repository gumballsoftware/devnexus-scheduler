package org.gumball.ajug.devnexus.desktop.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class SessionSpeakerId implements Serializable {
    private static final long serialVersionUID = -2674570985937598660L;
    @NotNull
    @Column(name = "speaker_id", nullable = false)
    private String speakerId;

    @NotNull
    @Column(name = "session_id", nullable = false)
    private String sessionId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        SessionSpeakerId entity = (SessionSpeakerId) o;
        return Objects.equals(this.speakerId, entity.speakerId) &&
                Objects.equals(this.sessionId, entity.sessionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(speakerId, sessionId);
    }

}