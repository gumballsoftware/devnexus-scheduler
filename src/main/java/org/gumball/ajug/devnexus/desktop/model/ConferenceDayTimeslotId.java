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
public class ConferenceDayTimeslotId implements Serializable {
    private static final long serialVersionUID = -3131197854212280133L;
    @NotNull
    @Column(name = "timeslot_id", nullable = false)
    private Integer timeslotId;

    @NotNull
    @Column(name = "conference_day_id", nullable = false)
    private Integer conferenceDayId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ConferenceDayTimeslotId entity = (ConferenceDayTimeslotId) o;
        return Objects.equals(this.timeslotId, entity.timeslotId) &&
                Objects.equals(this.conferenceDayId, entity.conferenceDayId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timeslotId, conferenceDayId);
    }

}