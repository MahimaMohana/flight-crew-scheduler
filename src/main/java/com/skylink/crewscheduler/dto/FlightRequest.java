package com.skylink.crewscheduler.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record FlightRequest(
        @NotBlank String flightNumber,
        @NotBlank String originAirport,
        @NotBlank String destinationAirport,
        @NotNull LocalDateTime departureTime,
        @NotNull LocalDateTime arrivalTime,
        String aircraftType
) {
}
