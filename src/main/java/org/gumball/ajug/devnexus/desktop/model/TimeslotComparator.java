package org.gumball.ajug.devnexus.desktop.model;

import java.util.Comparator;

public class TimeslotComparator implements Comparator<Timeslot> {
    @Override
    public int compare(Timeslot t1, Timeslot t2) {
        if (t1.getConferenceDayNumber() == t2.getConferenceDayNumber()) {
            return t1.getStarttime().compareTo(t2.getStarttime());
        }
        return t1.getConferenceDayNumber() - t2.getConferenceDayNumber();
    }
}
