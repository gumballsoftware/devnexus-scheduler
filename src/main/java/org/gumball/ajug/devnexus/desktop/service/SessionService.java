package org.gumball.ajug.devnexus.desktop.service;

import org.gumball.ajug.devnexus.desktop.parser.EventParser;
import lombok.RequiredArgsConstructor;
import org.gumball.ajug.devnexus.desktop.repository.SessionRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class SessionService {
    private final SessionRepository sessionRepository;

    private final EventParser eventParser;

    public void parse() throws IOException {
        eventParser.parse();
    }
}
