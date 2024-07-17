package org.gumball.ajug.devnexus.desktop.repository;

import org.gumball.ajug.devnexus.desktop.model.ScheduledSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduledSessionRepository extends JpaRepository<ScheduledSession, Integer> {
}
