package com.skylink.crewscheduler.repository;

import com.skylink.crewscheduler.model.Flight;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FlightRepository extends JpaRepository<Flight, Long> {

    List<Flight> findByTripId(Long tripId);
}
