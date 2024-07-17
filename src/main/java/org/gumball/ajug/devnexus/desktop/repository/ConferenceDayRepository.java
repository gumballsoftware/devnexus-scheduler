package org.gumball.ajug.devnexus.desktop.repository;

import org.gumball.ajug.devnexus.desktop.model.ConferenceDay;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConferenceDayRepository extends JpaRepository<ConferenceDay, Integer> {
}