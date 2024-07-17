package org.gumball.ajug.devnexus.desktop.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Entity
@Table(name = "scheduled_session", schema = "public")
public class ScheduledSession {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "scheduled_session_id_gen")
    @SequenceGenerator(name = "scheduled_session_id_gen", sequenceName = "scheduled_session_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns( {
            @JoinColumn(name = "conference_day_id"),
            @JoinColumn(name = "timeslot_id")
    })
    private ConferenceDayTimeslot conferenceDayTimeslot;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id")
    private Session session;
}
