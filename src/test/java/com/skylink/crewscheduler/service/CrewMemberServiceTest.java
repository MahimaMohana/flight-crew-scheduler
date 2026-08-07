package com.skylink.crewscheduler.service;

import com.skylink.crewscheduler.dto.CrewMemberCreateRequest;
import com.skylink.crewscheduler.dto.CrewMemberDto;
import com.skylink.crewscheduler.exception.DuplicateResourceException;
import com.skylink.crewscheduler.exception.ResourceNotFoundException;
import com.skylink.crewscheduler.model.CrewMember;
import com.skylink.crewscheduler.model.CrewRole;
import com.skylink.crewscheduler.repository.CrewMemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CrewMemberServiceTest {

    @Mock
    private CrewMemberRepository crewMemberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private CrewMemberService crewMemberService;

    private CrewMemberCreateRequest request;

    @BeforeEach
    void setUp() {
        request = new CrewMemberCreateRequest(
                "SW30001", "newhire", "CrewDemo#2026", "Nina", "Hale",
                "nina.hale@skylink-demo.com", CrewRole.FLIGHT_ATTENDANT, "DAL", 2001);
    }

    @Test
    void createEncodesPasswordAndSavesCrewMember() {
        when(crewMemberRepository.existsByUsername("newhire")).thenReturn(false);
        when(passwordEncoder.encode("CrewDemo#2026")).thenReturn("encoded-hash");
        when(crewMemberRepository.save(any(CrewMember.class))).thenAnswer(invocation -> {
            CrewMember saved = invocation.getArgument(0);
            saved.setId(42L);
            return saved;
        });

        CrewMemberDto result = crewMemberService.create(request);

        assertThat(result.id()).isEqualTo(42L);
        assertThat(result.username()).isEqualTo("newhire");
        assertThat(result.crewRole()).isEqualTo(CrewRole.FLIGHT_ATTENDANT);
    }

    @Test
    void createRejectsDuplicateUsername() {
        when(crewMemberRepository.existsByUsername("newhire")).thenReturn(true);

        assertThatThrownBy(() -> crewMemberService.create(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("newhire");
    }

    @Test
    void deleteThrowsWhenCrewMemberMissing() {
        when(crewMemberRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> crewMemberService.delete(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getByUsernameOrThrowReturnsMatch() {
        CrewMember crewMember = new CrewMember("SW10231", "jmorrison", "hash", "Jordan", "Morrison",
                "jordan.morrison@skylink-demo.com", CrewRole.PILOT, "DAL", 412);
        when(crewMemberRepository.findByUsername("jmorrison")).thenReturn(Optional.of(crewMember));

        CrewMember result = crewMemberService.getByUsernameOrThrow("jmorrison");

        assertThat(result.getFullName()).isEqualTo("Jordan Morrison");
    }
}
