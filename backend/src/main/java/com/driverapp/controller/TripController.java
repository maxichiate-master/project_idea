package com.driverapp.controller;

import com.driverapp.dto.RatingRequest;
import com.driverapp.dto.TripCreateRequest;
import com.driverapp.model.Rating;
import com.driverapp.model.TripRequest;
import com.driverapp.model.User;
import com.driverapp.service.TripService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trips")
@RequiredArgsConstructor
public class TripController {

    private final TripService tripService;

    @PostMapping
    public ResponseEntity<TripRequest> createTrip(@AuthenticationPrincipal User user,
                                                  @Valid @RequestBody TripCreateRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tripService.createRequest(user, req));
    }

    @GetMapping("/active")
    public ResponseEntity<TripRequest> getActiveTrip(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(tripService.getActiveTrip(user));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<TripRequest> cancelTrip(@AuthenticationPrincipal User user,
                                                  @PathVariable Long id) {
        return ResponseEntity.ok(tripService.cancelTrip(user, id));
    }

    @PostMapping("/rate")
    public ResponseEntity<Rating> rateTrip(@AuthenticationPrincipal User user,
                                           @Valid @RequestBody RatingRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tripService.rateTrip(user, req));
    }
}
