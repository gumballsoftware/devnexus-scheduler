package org.gumball.ajug.devnexus.desktop.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.ColumnDefault;


@Getter
@Setter
@Entity
@Table(name = "room", schema = "public")
public class Room {
    @Id
    @ColumnDefault("nextval('room_id_seq'::regclass)")
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "room_topic", length = Integer.MAX_VALUE)
    private String name;
}