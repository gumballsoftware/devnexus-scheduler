package org.gumball.ajug.devnexus.desktop.repository;

import org.gumball.ajug.devnexus.desktop.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionRepository extends JpaRepository<Session, Integer> {
}