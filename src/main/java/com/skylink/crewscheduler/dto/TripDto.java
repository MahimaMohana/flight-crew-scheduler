package com.skylink.crewscheduler.dto;

import com.skylink.crewscheduler.model.Trip;
import com.skylink.crewscheduler.model.TripStatus;

import java.time.LocalDate;
import java.util.List;

public record TripDto(
        Long id,
        String tripNumber,
        LocalDate startDate,
        LocalDate endDate,
        TripStatus status,
        List<FlightDto> flights
) {

    public static TripDto from(Trip trip) {
        List<FlightDto> flightDtos = trip.getFlights().stream()
                .map(FlightDto::from)
                .toList();
        return new TripDto(
                trip.getId(),
                trip.getTripNumber(),
                trip.getStartDate(),
                trip.getEndDate(),
                trip.getStatus(),
                flightDtos
        );
    }

    public static TripDto summaryFrom(Trip trip) {
        return new TripDto(
                trip.getId(),
                trip.getTripNumber(),
                trip.getStartDate(),
                trip.getEndDate(),
                trip.getStatus(),
                List.of()
        );
    }
}
