package com.skylink.crewscheduler.controller;

import com.skylink.crewscheduler.dto.TripCreateRequest;
import com.skylink.crewscheduler.dto.TripDto;
import com.skylink.crewscheduler.service.TripService;
import jakarta.validation.Valid;
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
@RequestMapping("/api/trips")
public class TripController {

    private final TripService tripService;

    public TripController(TripService tripService) {
        this.tripService = tripService;
    }

    @GetMapping
    public List<TripDto> getAll() {
        return tripService.findAll();
    }

    @GetMapping("/{id}")
    public TripDto getById(@PathVariable Long id) {
        return tripService.findById(id);
    }

    @PostMapping
    public ResponseEntity<TripDto> create(@Valid @RequestBody TripCreateRequest request) {
        TripDto created = tripService.create(request);
        return ResponseEntity.created(URI.create("/api/trips/" + created.id())).body(created);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tripService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
