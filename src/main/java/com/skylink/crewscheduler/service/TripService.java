package com.skylink.crewscheduler.service;

import com.skylink.crewscheduler.dto.FlightRequest;
import com.skylink.crewscheduler.dto.TripCreateRequest;
import com.skylink.crewscheduler.dto.TripDto;
import com.skylink.crewscheduler.exception.DuplicateResourceException;
import com.skylink.crewscheduler.exception.ResourceNotFoundException;
import com.skylink.crewscheduler.model.Flight;
import com.skylink.crewscheduler.model.Trip;
import com.skylink.crewscheduler.model.TripStatus;
import com.skylink.crewscheduler.repository.TripRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TripService {

    private final TripRepository tripRepository;

    public TripService(TripRepository tripRepository) {
        this.tripRepository = tripRepository;
    }

    @Transactional(readOnly = true)
    public List<TripDto> findAll() {
        return tripRepository.findAll().stream()
                .map(TripDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public TripDto findById(Long id) {
        return TripDto.from(getTripOrThrow(id));
    }

    @Transactional(readOnly = true)
    public Trip getTripOrThrow(Long id) {
        return tripRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found with id " + id));
    }

    public TripDto create(TripCreateRequest request) {
        if (tripRepository.findByTripNumber(request.tripNumber()).isPresent()) {
            throw new DuplicateResourceException("Trip number already in use: " + request.tripNumber());
        }
        if (request.endDate().isBefore(request.startDate())) {
            throw new IllegalArgumentException("Trip endDate cannot be before startDate");
        }

        Trip trip = new Trip(request.tripNumber(), request.startDate(), request.endDate(), TripStatus.SCHEDULED);

        for (FlightRequest flightRequest : request.flights()) {
            Flight flight = new Flight(
                    flightRequest.flightNumber(),
                    flightRequest.originAirport(),
                    flightRequest.destinationAirport(),
                    flightRequest.departureTime(),
                    flightRequest.arrivalTime(),
                    flightRequest.aircraftType()
            );
            trip.addFlight(flight);
        }

        return TripDto.from(tripRepository.save(trip));
    }

    public void delete(Long id) {
        if (!tripRepository.existsById(id)) {
            throw new ResourceNotFoundException("Trip not found with id " + id);
        }
        tripRepository.deleteById(id);
    }
}
