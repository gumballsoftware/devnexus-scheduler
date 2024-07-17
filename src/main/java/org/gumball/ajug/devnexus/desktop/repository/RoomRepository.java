package org.gumball.ajug.devnexus.desktop.repository;

import org.gumball.ajug.devnexus.desktop.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Integer> {
}