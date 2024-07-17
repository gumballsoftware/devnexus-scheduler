package org.gumball.ajug.devnexus.desktop.service;

import org.gumball.ajug.devnexus.desktop.config.IngestProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
@RequiredArgsConstructor
public class IngestService {
    private final IngestProperties ingestProperties;

    public void ingest() throws IOException {
        InputStream in = new URL(ingestProperties.getScheduleURL()).openStream();
        Files.copy(in, Paths.get(ingestProperties.getScheduleFileName()), StandardCopyOption.REPLACE_EXISTING);
        in = new URL(ingestProperties.getEventsURL()).openStream();
        Files.copy(in, Paths.get(ingestProperties.getEventsFileName()), StandardCopyOption.REPLACE_EXISTING);
        in = new URL(ingestProperties.getSpeakersURL()).openStream();
        Files.copy(in, Paths.get(ingestProperties.getSpeakersFileName()), StandardCopyOption.REPLACE_EXISTING);
    }
}
