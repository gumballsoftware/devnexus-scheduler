package org.gumball.ajug.devnexus.desktop.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("ingest")
public class IngestProperties {

//    @Value( "${scheduleURL}" )
    private String scheduleURL;
//    @Value("${scheduleFileName}")
    private String scheduleFileName;

//    @Value("${eventsURL}")
    private String eventsURL;
//    @Value("${eventsFileName}")
    private String eventsFileName;

//    @Value("${speakersURL}")
    private String speakersURL;
//    @Value("${speakersFileName}")
    private String speakersFileName;

    public String getScheduleURL() {
        return scheduleURL;
    }
    public String getScheduleFileName() {
        return scheduleFileName;
    }
    public String getEventsURL() {return eventsURL;}
    public String getEventsFileName() {return eventsFileName;}
    public String getSpeakersURL() {return speakersURL;}
    public String getSpeakersFileName() {return speakersFileName;}
}
