package com.skylink.crewscheduler.config;

import com.skylink.crewscheduler.model.AssignmentRole;
import com.skylink.crewscheduler.model.AssignmentStatus;
import com.skylink.crewscheduler.model.CrewMember;
import com.skylink.crewscheduler.model.CrewRole;
import com.skylink.crewscheduler.model.Flight;
import com.skylink.crewscheduler.model.Trip;
import com.skylink.crewscheduler.model.TripAssignment;
import com.skylink.crewscheduler.model.TripStatus;
import com.skylink.crewscheduler.repository.CrewMemberRepository;
import com.skylink.crewscheduler.repository.TripAssignmentRepository;
import com.skylink.crewscheduler.repository.TripRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Populates the in-memory H2 database with a believable roster, a month of
 * trips, and assignments on startup, so the crew portal has data to show the
 * moment the demo launches. Runs only against an empty database, so it is
 * safe across repeated {@code mvn spring-boot:run} restarts.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final CrewMemberRepository crewMemberRepository;
    private final TripRepository tripRepository;
    private final TripAssignmentRepository tripAssignmentRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(CrewMemberRepository crewMemberRepository,
                       TripRepository tripRepository,
                       TripAssignmentRepository tripAssignmentRepository,
                       PasswordEncoder passwordEncoder) {
        this.crewMemberRepository = crewMemberRepository;
        this.tripRepository = tripRepository;
        this.tripAssignmentRepository = tripAssignmentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (crewMemberRepository.count() > 0) {
            return;
        }

        CrewMember captainMorrison = crewMember("SW10231", "jmorrison", "Jordan", "Morrison",
                "jordan.morrison@skylink-demo.com", CrewRole.PILOT, "DAL", 412);
        CrewMember foSingh = crewMember("SW10456", "asingh", "Amara", "Singh",
                "amara.singh@skylink-demo.com", CrewRole.PILOT, "DAL", 1287);
        CrewMember purserWilliams = crewMember("SW20118", "kwilliams", "Keisha", "Williams",
                "keisha.williams@skylink-demo.com", CrewRole.FLIGHT_ATTENDANT, "DAL", 305);
        CrewMember faOrtega = crewMember("SW20873", "rortega", "Rafael", "Ortega",
                "rafael.ortega@skylink-demo.com", CrewRole.FLIGHT_ATTENDANT, "DAL", 954);
        CrewMember faNguyen = crewMember("SW21190", "tnguyen", "Thao", "Nguyen",
                "thao.nguyen@skylink-demo.com", CrewRole.FLIGHT_ATTENDANT, "HOU", 1102);

        crewMemberRepository.saveAll(java.util.List.of(
                captainMorrison, foSingh, purserWilliams, faOrtega, faNguyen));

        LocalDate today = LocalDate.now();
        LocalDate monthStart = today.withDayOfMonth(1);

        Trip turn = trip("TRIP-4401", monthStart.plusDays(2), monthStart.plusDays(2));
        turn.addFlight(flight("SL1042", "DAL", "HOU", monthStart.plusDays(2), LocalTime.of(7, 30), 65));
        turn.addFlight(flight("SL1043", "HOU", "DAL", monthStart.plusDays(2), LocalTime.of(9, 45), 65));

        Trip layover = trip("TRIP-4402", monthStart.plusDays(5), monthStart.plusDays(7));
        layover.addFlight(flight("SL2210", "DAL", "MDW", monthStart.plusDays(5), LocalTime.of(11, 0), 130));
        layover.addFlight(flight("SL2231", "MDW", "DEN", monthStart.plusDays(6), LocalTime.of(14, 20), 95));
        layover.addFlight(flight("SL2244", "DEN", "DAL", monthStart.plusDays(7), LocalTime.of(10, 10), 100));

        Trip westCoast = trip("TRIP-4403", monthStart.plusDays(10), monthStart.plusDays(11));
        westCoast.addFlight(flight("SL3305", "DAL", "LAS", monthStart.plusDays(10), LocalTime.of(8, 15), 150));
        westCoast.addFlight(flight("SL3312", "LAS", "DAL", monthStart.plusDays(11), LocalTime.of(16, 5), 150));

        Trip quickTurn = trip("TRIP-4404", monthStart.plusDays(15), monthStart.plusDays(15));
        quickTurn.addFlight(flight("SL1108", "DAL", "MCI", monthStart.plusDays(15), LocalTime.of(6, 0), 65));
        quickTurn.addFlight(flight("SL1109", "MCI", "DAL", monthStart.plusDays(15), LocalTime.of(8, 20), 65));

        Trip southeastRun = trip("TRIP-4405", monthStart.plusDays(19), monthStart.plusDays(21));
        southeastRun.addFlight(flight("SL5501", "DAL", "ATL", monthStart.plusDays(19), LocalTime.of(13, 0), 130));
        southeastRun.addFlight(flight("SL5518", "ATL", "MCO", monthStart.plusDays(20), LocalTime.of(9, 30), 130));
        southeastRun.addFlight(flight("SL5527", "MCO", "DAL", monthStart.plusDays(21), LocalTime.of(17, 45), 130));

        Trip nextMonthRun = trip("TRIP-4406", monthStart.plusMonths(1).plusDays(2), monthStart.plusMonths(1).plusDays(3));
        nextMonthRun.addFlight(flight("SL1876", "DAL", "PHX", monthStart.plusMonths(1).plusDays(2), LocalTime.of(12, 40), 100));
        nextMonthRun.addFlight(flight("SL1889", "PHX", "DAL", monthStart.plusMonths(1).plusDays(3), LocalTime.of(15, 55), 100));

        tripRepository.saveAll(java.util.List.of(turn, layover, westCoast, quickTurn, southeastRun, nextMonthRun));

        assign(captainMorrison, turn, AssignmentRole.CAPTAIN, AssignmentStatus.CONFIRMED);
        assign(captainMorrison, layover, AssignmentRole.CAPTAIN, AssignmentStatus.CONFIRMED);
        assign(captainMorrison, westCoast, AssignmentRole.CAPTAIN, AssignmentStatus.ASSIGNED);
        assign(captainMorrison, southeastRun, AssignmentRole.CAPTAIN, AssignmentStatus.ASSIGNED);
        assign(captainMorrison, nextMonthRun, AssignmentRole.CAPTAIN, AssignmentStatus.ASSIGNED);

        assign(foSingh, turn, AssignmentRole.FIRST_OFFICER, AssignmentStatus.CONFIRMED);
        assign(foSingh, westCoast, AssignmentRole.FIRST_OFFICER, AssignmentStatus.ASSIGNED);
        assign(foSingh, quickTurn, AssignmentRole.FIRST_OFFICER, AssignmentStatus.CONFIRMED);

        assign(purserWilliams, layover, AssignmentRole.PURSER, AssignmentStatus.CONFIRMED);
        assign(purserWilliams, southeastRun, AssignmentRole.PURSER, AssignmentStatus.ASSIGNED);

        assign(faOrtega, turn, AssignmentRole.FLIGHT_ATTENDANT, AssignmentStatus.CONFIRMED);
        assign(faOrtega, layover, AssignmentRole.FLIGHT_ATTENDANT, AssignmentStatus.CONFIRMED);
        assign(faOrtega, quickTurn, AssignmentRole.FLIGHT_ATTENDANT, AssignmentStatus.ASSIGNED);

        assign(faNguyen, westCoast, AssignmentRole.FLIGHT_ATTENDANT, AssignmentStatus.ASSIGNED);
        assign(faNguyen, southeastRun, AssignmentRole.FLIGHT_ATTENDANT, AssignmentStatus.ASSIGNED);
        assign(faNguyen, nextMonthRun, AssignmentRole.FLIGHT_ATTENDANT, AssignmentStatus.ASSIGNED);
    }

    private CrewMember crewMember(String employeeId, String username, String firstName, String lastName,
                                   String email, CrewRole role, String base, int seniority) {
        // Shared demo password for every seeded crew member: "CrewDemo#2026"
        return new CrewMember(employeeId, username, passwordEncoder.encode("CrewDemo#2026"),
                firstName, lastName, email, role, base, seniority);
    }

    private Trip trip(String tripNumber, LocalDate start, LocalDate end) {
        return new Trip(tripNumber, start, end, TripStatus.SCHEDULED);
    }

    private Flight flight(String flightNumber, String origin, String destination,
                           LocalDate date, LocalTime departure, int durationMinutes) {
        LocalDateTime departureTime = LocalDateTime.of(date, departure);
        return new Flight(flightNumber, origin, destination, departureTime,
                departureTime.plusMinutes(durationMinutes), "Boeing 737-800");
    }

    private void assign(CrewMember crewMember, Trip trip, AssignmentRole role, AssignmentStatus status) {
        tripAssignmentRepository.save(new TripAssignment(crewMember, trip, role, status, LocalDate.now()));
    }
}
