package com.skylink.crewscheduler.repository;

import com.skylink.crewscheduler.model.CrewMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CrewMemberRepository extends JpaRepository<CrewMember, Long> {

    Optional<CrewMember> findByUsername(String username);

    Optional<CrewMember> findByEmployeeId(String employeeId);

    boolean existsByUsername(String username);
}
