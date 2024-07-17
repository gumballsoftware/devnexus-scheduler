package org.gumball.ajug.devnexus.desktop.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Entity
@Table(name = "conference_day_timeslot", schema = "public")
public class ConferenceDayTimeslot {
    @EmbeddedId
    private ConferenceDayTimeslotId id;

    @MapsId("timeslotId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "timeslot_id", nullable = false)
    private Timeslot timeslot;

    @MapsId("conferenceDayId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "conference_day_id", nullable = false)
    private ConferenceDay conferenceDay;

}