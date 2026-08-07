package com.skylink.crewscheduler.controller;

import com.skylink.crewscheduler.dto.CrewMemberCreateRequest;
import com.skylink.crewscheduler.dto.CrewMemberDto;
import com.skylink.crewscheduler.service.CrewMemberService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/crew-members")
public class CrewMemberController {

    private final CrewMemberService crewMemberService;

    public CrewMemberController(CrewMemberService crewMemberService) {
        this.crewMemberService = crewMemberService;
    }

    @GetMapping
    public List<CrewMemberDto> getAll() {
        return crewMemberService.findAll();
    }

    @GetMapping("/{id}")
    public CrewMemberDto getById(@PathVariable Long id) {
        return crewMemberService.findById(id);
    }

    @PostMapping
    public ResponseEntity<CrewMemberDto> create(@Valid @RequestBody CrewMemberCreateRequest request) {
        CrewMemberDto created = crewMemberService.create(request);
        return ResponseEntity.created(URI.create("/api/crew-members/" + created.id())).body(created);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        crewMemberService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
