package com.skylink.crewscheduler.config;

import com.skylink.crewscheduler.model.CrewMember;
import com.skylink.crewscheduler.repository.CrewMemberRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Adapts {@link CrewMember} records to Spring Security's {@link UserDetails}
 * so the same table used for scheduling doubles as the crew portal's login
 * store, keeping this demo free of a separate identity system.
 */
@Service
public class CrewUserDetailsService implements UserDetailsService {

    private final CrewMemberRepository crewMemberRepository;

    public CrewUserDetailsService(CrewMemberRepository crewMemberRepository) {
        this.crewMemberRepository = crewMemberRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        CrewMember crewMember = crewMemberRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("No crew member with username " + username));

        return User.builder()
                .username(crewMember.getUsername())
                .password(crewMember.getPassword())
                .authorities(new SimpleGrantedAuthority("ROLE_" + crewMember.getCrewRole().name()))
                .build();
    }
}
