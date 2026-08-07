package com.skylink.crewscheduler.controller;

import com.skylink.crewscheduler.dto.CrewMemberDto;
import com.skylink.crewscheduler.model.CrewMember;
import com.skylink.crewscheduler.service.CrewMemberService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Tells the crew portal frontend who is currently logged in, so the calendar
 * page can greet the crew member and scope its "my calendar" API calls.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final CrewMemberService crewMemberService;

    public AuthController(CrewMemberService crewMemberService) {
        this.crewMemberService = crewMemberService;
    }

    @GetMapping("/me")
    public CrewMemberDto currentCrewMember(Authentication authentication) {
        CrewMember crewMember = crewMemberService.getByUsernameOrThrow(authentication.getName());
        return CrewMemberDto.from(crewMember);
    }
}
