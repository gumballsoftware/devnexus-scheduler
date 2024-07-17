package org.gumball.ajug.devnexus.model;

import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class TimeslotTest {

    @Test
    void shouldCreateTimeslotIdForEachMinute() {
        List<Timeslot> timeslots = new ArrayList<>();
        Set<Timeslot> timeslotSet = new HashSet<>();
        for (int day = 0; day < 7; day++) {
            for (int hour = 0; hour < 24; hour++) {
                for (int minute = 0; minute < 60; minute++) {
                    LocalTime localTime = LocalTime.of(hour, minute);
                    Timeslot timeslot = new Timeslot();
                    timeslot.setConferenceDay(day);
                    timeslot.setStarttime(localTime);
                    timeslots.add(timeslot);
                    timeslotSet.add(timeslot);
                }
            }
        }
        assertEquals(timeslotSet.size(), timeslots.size());
    }
}