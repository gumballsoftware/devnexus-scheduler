package org.gumball.ajug.devnexus.desktop.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@Entity
@Table(name = "timeslot", schema = "public")
public class Timeslot {
    @Id
    @ColumnDefault("nextval('timeslot_id_seq'::regclass)")
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "starttime")
    private LocalTime starttime;

    @Column(name = "endtime")
    private LocalTime endtime;

    @Column(name = "conference_day_number")
    private int conferenceDayNumber = 0;

    @Column(name = "conference_day_date")
    private LocalDate conferenceDayDate;

    public void setStarttime(LocalTime starttime) {
        this.starttime = starttime;
        id = this.conferenceDayNumber * 24 * 1000 + starttime.getHour() * 100 + starttime.getMinute();
    }

    public void setConferenceDay(int conferenceDay) {
        this.conferenceDayNumber = conferenceDay;
        id = this.conferenceDayNumber * 24 * 1000;
        if (starttime != null)
            id = id + starttime.getHour() * 100 + starttime.getMinute();
    }
}