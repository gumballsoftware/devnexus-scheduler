package org.gumball.ajug.devnexus.desktop.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("scheduler")
public class ApplicationProperties {

    private String scheduleAPIBaseURL="http://localhost:8080/schedule";

    private String scheduleURL;

    private String eventsURL;

    private String speakersURL;

    private String eventsFileName;

    private String scheduleFileName;

    private String speakersFileName;
}
