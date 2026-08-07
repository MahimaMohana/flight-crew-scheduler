package com.skylink.crewscheduler.repository;

import com.skylink.crewscheduler.model.TripAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TripAssignmentRepository extends JpaRepository<TripAssignment, Long> {

    List<TripAssignment> findByCrewMemberId(Long crewMemberId);

    List<TripAssignment> findByTripId(Long tripId);

    /**
     * All assignments for a crew member whose trip overlaps the given date
     * window. Backs the "my calendar" endpoint that the crew portal's
     * FullCalendar view calls with the visible month's start/end.
     */
    @Query("""
            SELECT ta FROM TripAssignment ta
            JOIN FETCH ta.trip t
            WHERE ta.crewMember.id = :crewMemberId
              AND t.startDate <= :rangeEnd
              AND t.endDate >= :rangeStart
            ORDER BY t.startDate ASC
            """)
    List<TripAssignment> findForCrewMemberBetween(
            @Param("crewMemberId") Long crewMemberId,
            @Param("rangeStart") LocalDate rangeStart,
            @Param("rangeEnd") LocalDate rangeEnd);
}
