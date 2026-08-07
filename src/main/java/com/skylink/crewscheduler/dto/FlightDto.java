package com.skylink.crewscheduler.dto;

import com.skylink.crewscheduler.model.Flight;

import java.time.LocalDateTime;

public record FlightDto(
        Long id,
        String flightNumber,
        String originAirport,
        String destinationAirport,
        LocalDateTime departureTime,
        LocalDateTime arrivalTime,
        String aircraftType
) {

    public static FlightDto from(Flight flight) {
        return new FlightDto(
                flight.getId(),
                flight.getFlightNumber(),
                flight.getOriginAirport(),
                flight.getDestinationAirport(),
                flight.getDepartureTime(),
                flight.getArrivalTime(),
                flight.getAircraftType()
        );
    }
}
