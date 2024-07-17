package org.gumball.ajug.devnexus.desktop.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Cascade;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "session", schema = "public")
public class Session {
    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Size(max = 255)
    @Column(name = "title")
    private String title;

    @Column(name = "description", length = Integer.MAX_VALUE)
    private String description;

    @Column(name = "starts")
    private Instant starts;

    @Column(name = "ends")
    private Instant ends;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room sessionRoom;

    @Column(name = "track")
    private String track;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<SessionSpeaker> sessionSpeakers = new ArrayList<>();

    public Session merge(Session session) {
        if (StringUtils.isNotEmpty(session.getTitle())) {
            this.title = session.getTitle();
        }
        if (StringUtils.isNotEmpty(session.getDescription())) {
            this.description = session.getDescription();
        }
        if (Objects.nonNull(session.getStarts())) {
            this.starts = session.getStarts();
        }
        if (Objects.nonNull(session.getEnds())) {
            this.ends = session.getEnds();
        }
        return this;
    }
}