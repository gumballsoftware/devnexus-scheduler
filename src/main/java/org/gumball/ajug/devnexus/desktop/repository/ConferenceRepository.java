package org.gumball.ajug.devnexus.desktop.repository;

import org.gumball.ajug.devnexus.desktop.model.Conference;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConferenceRepository extends JpaRepository<Conference, Integer> {
}