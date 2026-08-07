package com.skylink.crewscheduler.service;

import com.skylink.crewscheduler.dto.AssignmentStatusUpdateRequest;
import com.skylink.crewscheduler.dto.CalendarEventDto;
import com.skylink.crewscheduler.dto.TripAssignmentCreateRequest;
import com.skylink.crewscheduler.dto.TripAssignmentDto;
import com.skylink.crewscheduler.exception.DuplicateResourceException;
import com.skylink.crewscheduler.exception.ResourceNotFoundException;
import com.skylink.crewscheduler.model.AssignmentRole;
import com.skylink.crewscheduler.model.AssignmentStatus;
import com.skylink.crewscheduler.model.CrewMember;
import com.skylink.crewscheduler.model.CrewRole;
import com.skylink.crewscheduler.model.Trip;
import com.skylink.crewscheduler.model.TripAssignment;
import com.skylink.crewscheduler.model.TripStatus;
import com.skylink.crewscheduler.repository.TripAssignmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TripAssignmentServiceTest {

    @Mock
    private TripAssignmentRepository tripAssignmentRepository;

    @Mock
    private CrewMemberService crewMemberService;

    @Mock
    private TripService tripService;

    @InjectMocks
    private TripAssignmentService tripAssignmentService;

    private CrewMember pilot;
    private CrewMember flightAttendant;
    private Trip julyTrip;

    @BeforeEach
    void setUp() {
        pilot = new CrewMember("SW10231", "jmorrison", "hash", "Jordan", "Morrison",
                "jordan.morrison@skylink-demo.com", CrewRole.PILOT, "DAL", 412);
        pilot.setId(1L);

        flightAttendant = new CrewMember("SW20873", "rortega", "hash", "Rafael", "Ortega",
                "rafael.ortega@skylink-demo.com", CrewRole.FLIGHT_ATTENDANT, "DAL", 954);
        flightAttendant.setId(2L);

        julyTrip = new Trip("TRIP-4401", LocalDate.of(2026, 7, 14), LocalDate.of(2026, 7, 16), TripStatus.SCHEDULED);
        julyTrip.setId(100L);
    }

    @Test
    void createAssignsPilotAsCaptainSuccessfully() {
        when(crewMemberService.getCrewMemberOrThrow(1L)).thenReturn(pilot);
        when(tripService.getTripOrThrow(100L)).thenReturn(julyTrip);
        when(tripAssignmentRepository.findByCrewMemberId(1L)).thenReturn(List.of());
        when(tripAssignmentRepository.save(any(TripAssignment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        TripAssignmentCreateRequest request = new TripAssignmentCreateRequest(1L, 100L, AssignmentRole.CAPTAIN);

        TripAssignmentDto result = tripAssignmentService.create(request);

        assertThat(result.assignmentRole()).isEqualTo(AssignmentRole.CAPTAIN);
        assertThat(result.status()).isEqualTo(AssignmentStatus.ASSIGNED);
        verify(tripAssignmentRepository, times(1)).save(any(TripAssignment.class));
    }

    @Test
    void createRejectsPilotAssignedAsPurser() {
        when(crewMemberService.getCrewMemberOrThrow(1L)).thenReturn(pilot);
        when(tripService.getTripOrThrow(100L)).thenReturn(julyTrip);

        TripAssignmentCreateRequest request = new TripAssignmentCreateRequest(1L, 100L, AssignmentRole.PURSER);

        assertThatThrownBy(() -> tripAssignmentService.create(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("PURSER");
    }

    @Test
    void createRejectsFlightAttendantAssignedAsFirstOfficer() {
        when(crewMemberService.getCrewMemberOrThrow(2L)).thenReturn(flightAttendant);
        when(tripService.getTripOrThrow(100L)).thenReturn(julyTrip);

        TripAssignmentCreateRequest request = new TripAssignmentCreateRequest(2L, 100L, AssignmentRole.FIRST_OFFICER);

        assertThatThrownBy(() -> tripAssignmentService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void createRejectsOverlappingTripForSameCrewMember() {
        Trip overlappingTrip = new Trip("TRIP-4402", LocalDate.of(2026, 7, 15), LocalDate.of(2026, 7, 18), TripStatus.SCHEDULED);
        overlappingTrip.setId(101L);

        TripAssignment existing = new TripAssignment(pilot, julyTrip, AssignmentRole.CAPTAIN,
                AssignmentStatus.CONFIRMED, LocalDate.of(2026, 7, 1));

        when(crewMemberService.getCrewMemberOrThrow(1L)).thenReturn(pilot);
        when(tripService.getTripOrThrow(101L)).thenReturn(overlappingTrip);
        when(tripAssignmentRepository.findByCrewMemberId(1L)).thenReturn(List.of(existing));

        TripAssignmentCreateRequest request = new TripAssignmentCreateRequest(1L, 101L, AssignmentRole.CAPTAIN);

        assertThatThrownBy(() -> tripAssignmentService.create(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("overlapping");
    }

    @Test
    void createAllowsBackToBackNonOverlappingTrips() {
        Trip laterTrip = new Trip("TRIP-4403", LocalDate.of(2026, 7, 17), LocalDate.of(2026, 7, 19), TripStatus.SCHEDULED);
        laterTrip.setId(102L);

        TripAssignment existing = new TripAssignment(pilot, julyTrip, AssignmentRole.CAPTAIN,
                AssignmentStatus.CONFIRMED, LocalDate.of(2026, 7, 1));

        when(crewMemberService.getCrewMemberOrThrow(1L)).thenReturn(pilot);
        when(tripService.getTripOrThrow(102L)).thenReturn(laterTrip);
        when(tripAssignmentRepository.findByCrewMemberId(1L)).thenReturn(List.of(existing));
        when(tripAssignmentRepository.save(any(TripAssignment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        TripAssignmentCreateRequest request = new TripAssignmentCreateRequest(1L, 102L, AssignmentRole.CAPTAIN);

        TripAssignmentDto result = tripAssignmentService.create(request);

        assertThat(result.trip().tripNumber()).isEqualTo("TRIP-4403");
    }

    @Test
    void createIgnoresCancelledAssignmentsWhenCheckingOverlap() {
        Trip overlappingTrip = new Trip("TRIP-4402", LocalDate.of(2026, 7, 15), LocalDate.of(2026, 7, 18), TripStatus.SCHEDULED);
        overlappingTrip.setId(101L);

        TripAssignment cancelled = new TripAssignment(pilot, julyTrip, AssignmentRole.CAPTAIN,
                AssignmentStatus.CANCELLED, LocalDate.of(2026, 7, 1));

        when(crewMemberService.getCrewMemberOrThrow(1L)).thenReturn(pilot);
        when(tripService.getTripOrThrow(101L)).thenReturn(overlappingTrip);
        when(tripAssignmentRepository.findByCrewMemberId(1L)).thenReturn(List.of(cancelled));
        when(tripAssignmentRepository.save(any(TripAssignment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        TripAssignmentCreateRequest request = new TripAssignmentCreateRequest(1L, 101L, AssignmentRole.CAPTAIN);

        TripAssignmentDto result = tripAssignmentService.create(request);

        assertThat(result).isNotNull();
    }

    @Test
    void updateStatusChangesStatusOnExistingAssignment() {
        TripAssignment assignment = new TripAssignment(pilot, julyTrip, AssignmentRole.CAPTAIN,
                AssignmentStatus.ASSIGNED, LocalDate.of(2026, 7, 1));
        assignment.setId(500L);

        when(tripAssignmentRepository.findById(500L)).thenReturn(Optional.of(assignment));

        TripAssignmentDto result = tripAssignmentService.updateStatus(500L,
                new AssignmentStatusUpdateRequest(AssignmentStatus.CONFIRMED));

        assertThat(result.status()).isEqualTo(AssignmentStatus.CONFIRMED);
    }

    @Test
    void updateStatusThrowsWhenAssignmentMissing() {
        when(tripAssignmentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tripAssignmentService.updateStatus(999L,
                new AssignmentStatusUpdateRequest(AssignmentStatus.CONFIRMED)))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void findCalendarEventsMapsTripAssignmentsToCalendarEvents() {
        TripAssignment assignment = new TripAssignment(pilot, julyTrip, AssignmentRole.CAPTAIN,
                AssignmentStatus.CONFIRMED, LocalDate.of(2026, 7, 1));
        assignment.setId(500L);

        when(tripAssignmentRepository.findForCrewMemberBetween(anyLong(), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(List.of(assignment));

        List<CalendarEventDto> events = tripAssignmentService.findCalendarEvents(
                1L, LocalDate.of(2026, 7, 1), LocalDate.of(2026, 7, 31));

        assertThat(events).hasSize(1);
        assertThat(events.get(0).tripNumber()).isEqualTo("TRIP-4401");
        assertThat(events.get(0).color()).isEqualTo("#2BB3A3");
    }
}
