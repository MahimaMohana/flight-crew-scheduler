package com.skylink.crewscheduler.dto;

import com.skylink.crewscheduler.model.CrewMember;
import com.skylink.crewscheduler.model.CrewRole;

/**
 * Outward-facing view of a crew member. Deliberately omits the password
 * hash so it can never leak through a REST response.
 */
public record CrewMemberDto(
        Long id,
        String employeeId,
        String username,
        String firstName,
        String lastName,
        String email,
        CrewRole crewRole,
        String baseAirport,
        Integer seniorityNumber
) {

    public static CrewMemberDto from(CrewMember crewMember) {
        return new CrewMemberDto(
                crewMember.getId(),
                crewMember.getEmployeeId(),
                crewMember.getUsername(),
                crewMember.getFirstName(),
                crewMember.getLastName(),
                crewMember.getEmail(),
                crewMember.getCrewRole(),
                crewMember.getBaseAirport(),
                crewMember.getSeniorityNumber()
        );
    }
}
