package com.skylink.crewscheduler.controller;

import com.skylink.crewscheduler.dto.AssignmentStatusUpdateRequest;
import com.skylink.crewscheduler.dto.CalendarEventDto;
import com.skylink.crewscheduler.dto.TripAssignmentCreateRequest;
import com.skylink.crewscheduler.dto.TripAssignmentDto;
import com.skylink.crewscheduler.model.CrewMember;
import com.skylink.crewscheduler.service.CrewMemberService;
import com.skylink.crewscheduler.service.TripAssignmentService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/trip-assignments")
public class TripAssignmentController {

    private final TripAssignmentService tripAssignmentService;
    private final CrewMemberService crewMemberService;

    public TripAssignmentController(TripAssignmentService tripAssignmentService,
                                     CrewMemberService crewMemberService) {
        this.tripAssignmentService = tripAssignmentService;
        this.crewMemberService = crewMemberService;
    }

    @GetMapping
    public List<TripAssignmentDto> getAll() {
        return tripAssignmentService.findAll();
    }

    @GetMapping("/{id}")
    public TripAssignmentDto getById(@PathVariable Long id) {
        return tripAssignmentService.findById(id);
    }

    @GetMapping("/crew-member/{crewMemberId}")
    public List<TripAssignmentDto> getByCrewMember(@PathVariable Long crewMemberId) {
        return tripAssignmentService.findByCrewMember(crewMemberId);
    }

    /**
     * Calendar feed for the currently logged-in crew member. {@code start} and
     * {@code end} match the range FullCalendar.js passes when a month view
     * loads, so the frontend can wire this directly as its events source.
     */
    @GetMapping("/my-calendar")
    public List<CalendarEventDto> getMyCalendar(
            Authentication authentication,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {

        CrewMember crewMember = crewMemberService.getByUsernameOrThrow(authentication.getName());
        return tripAssignmentService.findCalendarEvents(crewMember.getId(), start, end);
    }

    @PostMapping
    public ResponseEntity<TripAssignmentDto> create(@Valid @RequestBody TripAssignmentCreateRequest request) {
        TripAssignmentDto created = tripAssignmentService.create(request);
        return ResponseEntity.created(URI.create("/api/trip-assignments/" + created.id())).body(created);
    }

    @PatchMapping("/{id}/status")
    public TripAssignmentDto updateStatus(@PathVariable Long id,
                                           @Valid @RequestBody AssignmentStatusUpdateRequest request) {
        return tripAssignmentService.updateStatus(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tripAssignmentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
