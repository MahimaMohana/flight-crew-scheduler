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
import com.skylink.crewscheduler.repository.TripAssignmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class TripAssignmentService {

    private static final Set<AssignmentRole> PILOT_ROLES = Set.of(AssignmentRole.CAPTAIN, AssignmentRole.FIRST_OFFICER);
    private static final Set<AssignmentRole> CABIN_ROLES = Set.of(AssignmentRole.PURSER, AssignmentRole.FLIGHT_ATTENDANT);

    private final TripAssignmentRepository tripAssignmentRepository;
    private final CrewMemberService crewMemberService;
    private final TripService tripService;

    public TripAssignmentService(TripAssignmentRepository tripAssignmentRepository,
                                  CrewMemberService crewMemberService,
                                  TripService tripService) {
        this.tripAssignmentRepository = tripAssignmentRepository;
        this.crewMemberService = crewMemberService;
        this.tripService = tripService;
    }

    @Transactional(readOnly = true)
    public List<TripAssignmentDto> findAll() {
        return tripAssignmentRepository.findAll().stream()
                .map(TripAssignmentDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public TripAssignmentDto findById(Long id) {
        return TripAssignmentDto.from(getAssignmentOrThrow(id));
    }

    @Transactional(readOnly = true)
    public List<TripAssignmentDto> findByCrewMember(Long crewMemberId) {
        return tripAssignmentRepository.findByCrewMemberId(crewMemberId).stream()
                .map(TripAssignmentDto::from)
                .toList();
    }

    /**
     * Builds the calendar feed for a single crew member within a date window.
     * Used by the "my calendar" endpoint the logged-in crew member's browser calls.
     */
    @Transactional(readOnly = true)
    public List<CalendarEventDto> findCalendarEvents(Long crewMemberId, LocalDate rangeStart, LocalDate rangeEnd) {
        return tripAssignmentRepository.findForCrewMemberBetween(crewMemberId, rangeStart, rangeEnd).stream()
                .map(CalendarEventDto::from)
                .toList();
    }

    public TripAssignmentDto create(TripAssignmentCreateRequest request) {
        CrewMember crewMember = crewMemberService.getCrewMemberOrThrow(request.crewMemberId());
        Trip trip = tripService.getTripOrThrow(request.tripId());

        validateRoleCompatibility(crewMember.getCrewRole(), request.assignmentRole());
        validateNoOverlap(crewMember, trip);

        TripAssignment assignment = new TripAssignment(
                crewMember,
                trip,
                request.assignmentRole(),
                AssignmentStatus.ASSIGNED,
                LocalDate.now()
        );

        return TripAssignmentDto.from(tripAssignmentRepository.save(assignment));
    }

    public TripAssignmentDto updateStatus(Long id, AssignmentStatusUpdateRequest request) {
        TripAssignment assignment = getAssignmentOrThrow(id);
        assignment.setStatus(request.status());
        return TripAssignmentDto.from(assignment);
    }

    public void delete(Long id) {
        if (!tripAssignmentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Trip assignment not found with id " + id);
        }
        tripAssignmentRepository.deleteById(id);
    }

    private TripAssignment getAssignmentOrThrow(Long id) {
        return tripAssignmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trip assignment not found with id " + id));
    }

    /**
     * Pilots may only be assigned as CAPTAIN or FIRST_OFFICER; flight attendants
     * may only be assigned as PURSER or FLIGHT_ATTENDANT.
     */
    private void validateRoleCompatibility(CrewRole crewRole, AssignmentRole assignmentRole) {
        boolean pilotMismatch = crewRole == CrewRole.PILOT && !PILOT_ROLES.contains(assignmentRole);
        boolean cabinMismatch = crewRole == CrewRole.FLIGHT_ATTENDANT && !CABIN_ROLES.contains(assignmentRole);

        if (pilotMismatch || cabinMismatch) {
            throw new IllegalArgumentException(
                    "Assignment role " + assignmentRole + " is not valid for crew role " + crewRole);
        }
    }

    /**
     * A crew member cannot be double-booked on two trips whose date ranges overlap.
     */
    private void validateNoOverlap(CrewMember crewMember, Trip trip) {
        boolean hasOverlap = tripAssignmentRepository.findByCrewMemberId(crewMember.getId()).stream()
                .filter(existing -> existing.getStatus() != AssignmentStatus.CANCELLED)
                .map(TripAssignment::getTrip)
                .anyMatch(existingTrip -> datesOverlap(existingTrip, trip));

        if (hasOverlap) {
            throw new DuplicateResourceException(
                    crewMember.getFullName() + " already has an overlapping trip assignment in that date range");
        }
    }

    private boolean datesOverlap(Trip a, Trip b) {
        return !a.getStartDate().isAfter(b.getEndDate()) && !b.getStartDate().isAfter(a.getEndDate());
    }
}
