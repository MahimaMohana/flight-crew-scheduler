package com.skylink.crewscheduler.service;

import com.skylink.crewscheduler.dto.CrewMemberCreateRequest;
import com.skylink.crewscheduler.dto.CrewMemberDto;
import com.skylink.crewscheduler.exception.DuplicateResourceException;
import com.skylink.crewscheduler.exception.ResourceNotFoundException;
import com.skylink.crewscheduler.model.CrewMember;
import com.skylink.crewscheduler.repository.CrewMemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CrewMemberService {

    private final CrewMemberRepository crewMemberRepository;
    private final PasswordEncoder passwordEncoder;

    public CrewMemberService(CrewMemberRepository crewMemberRepository, PasswordEncoder passwordEncoder) {
        this.crewMemberRepository = crewMemberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<CrewMemberDto> findAll() {
        return crewMemberRepository.findAll().stream()
                .map(CrewMemberDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public CrewMemberDto findById(Long id) {
        return CrewMemberDto.from(getCrewMemberOrThrow(id));
    }

    @Transactional(readOnly = true)
    public CrewMember getCrewMemberOrThrow(Long id) {
        return crewMemberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Crew member not found with id " + id));
    }

    @Transactional(readOnly = true)
    public CrewMember getByUsernameOrThrow(String username) {
        return crewMemberRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Crew member not found with username " + username));
    }

    public CrewMemberDto create(CrewMemberCreateRequest request) {
        if (crewMemberRepository.existsByUsername(request.username())) {
            throw new DuplicateResourceException("Username already in use: " + request.username());
        }

        CrewMember crewMember = new CrewMember(
                request.employeeId(),
                request.username(),
                passwordEncoder.encode(request.password()),
                request.firstName(),
                request.lastName(),
                request.email(),
                request.crewRole(),
                request.baseAirport(),
                request.seniorityNumber()
        );

        return CrewMemberDto.from(crewMemberRepository.save(crewMember));
    }

    public void delete(Long id) {
        if (!crewMemberRepository.existsById(id)) {
            throw new ResourceNotFoundException("Crew member not found with id " + id);
        }
        crewMemberRepository.deleteById(id);
    }
}
