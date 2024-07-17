package org.gumball.ajug.devnexus.desktop.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Entity
@Table(name = "speaker", schema = "public")
public class Speaker {
    public Speaker(String id, String speakerName) {
        this.id = id;
        this.speakerName = speakerName;
    }

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "speaker_name", length = Integer.MAX_VALUE)
    private String speakerName;

    @Column(name = "bio", length = Integer.MAX_VALUE)
    private String bio;

    @Column(name = "tagline", length = Integer.MAX_VALUE)
    private String tagline;

    public Speaker() {
    }
}