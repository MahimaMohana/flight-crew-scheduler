package com.skylink.crewscheduler.controller;

import com.skylink.crewscheduler.dto.CalendarEventDto;
import com.skylink.crewscheduler.config.SecurityConfig;
import com.skylink.crewscheduler.model.AssignmentRole;
import com.skylink.crewscheduler.model.AssignmentStatus;
import com.skylink.crewscheduler.model.CrewMember;
import com.skylink.crewscheduler.model.CrewRole;
import com.skylink.crewscheduler.service.CrewMemberService;
import com.skylink.crewscheduler.service.TripAssignmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TripAssignmentController.class)
@Import(SecurityConfig.class)
class TripAssignmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TripAssignmentService tripAssignmentService;

    @MockBean
    private CrewMemberService crewMemberService;

    @Test
    @WithMockUser(username = "jmorrison")
    void myCalendarReturnsEventsForLoggedInCrewMember() throws Exception {
        CrewMember crewMember = new CrewMember("SW10231", "jmorrison", "hash", "Jordan", "Morrison",
                "jordan.morrison@skylink-demo.com", CrewRole.PILOT, "DAL", 412);
        crewMember.setId(1L);

        CalendarEventDto event = new CalendarEventDto(
                500L, "TRIP-4401 \u00B7 CAPTAIN",
                LocalDate.of(2026, 7, 14), LocalDate.of(2026, 7, 17),
                "#2BB3A3", 100L, "TRIP-4401",
                AssignmentRole.CAPTAIN, AssignmentStatus.CONFIRMED,
                List.of("SL1042", "SL1043"));

        when(crewMemberService.getByUsernameOrThrow("jmorrison")).thenReturn(crewMember);
        when(tripAssignmentService.findCalendarEvents(eq(1L), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(List.of(event));

        mockMvc.perform(get("/api/trip-assignments/my-calendar")
                        .param("start", "2026-07-01")
                        .param("end", "2026-07-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tripNumber").value("TRIP-4401"))
                .andExpect(jsonPath("$[0].assignmentRole").value("CAPTAIN"));
    }

    @Test
    void myCalendarRequiresAuthentication() throws Exception {
        mockMvc.perform(get("/api/trip-assignments/my-calendar")
                        .param("start", "2026-07-01")
                        .param("end", "2026-07-31"))
                .andExpect(status().is3xxRedirection());
    }
}
