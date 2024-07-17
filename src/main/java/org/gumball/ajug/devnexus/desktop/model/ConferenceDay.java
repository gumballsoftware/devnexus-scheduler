package org.gumball.ajug.devnexus.desktop.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "conference_day", schema = "public")
public class ConferenceDay {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "conference_day_id_gen")
    @SequenceGenerator(name = "conference_day_id_gen", sequenceName = "conference_day_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "title", length = Integer.MAX_VALUE)
    private String title;

    @Column(name = "date")
    private Instant date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conference_id")
    private Conference conference;

    @OneToMany(mappedBy = "conferenceDay" , cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<ConferenceDayTimeslot> conferenceDayTimeslots = new ArrayList<>();

}