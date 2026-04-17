package com.driverapp.controller;

import com.driverapp.dto.DriverOnlineRequest;
import com.driverapp.dto.DriverUpgradeRequest;
import com.driverapp.model.DriverProfile;
import com.driverapp.model.TripRequest;
import com.driverapp.model.User;
import com.driverapp.service.DriverService;
import com.driverapp.service.TripService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/driver")
@RequiredArgsConstructor
public class DriverController {

    private final DriverService driverService;
    private final TripService tripService;

    @PostMapping("/upgrade")
    public ResponseEntity<DriverProfile> upgrade(@AuthenticationPrincipal User user,
                                                 @Valid @RequestBody DriverUpgradeRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(driverService.applyForDriver(user, req));
    }

    @PutMapping("/status")
    public ResponseEntity<DriverProfile> setStatus(@AuthenticationPrincipal User user,
                                                   @RequestBody DriverOnlineRequest req) {
        return ResponseEntity.ok(driverService.setOnlineStatus(user, req.isOnline(), req.getZone()));
    }

    @GetMapping("/requests")
    public ResponseEntity<List<TripRequest>> getAvailableRequests(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(tripService.getAvailableTripsForDriver(user));
    }

    @PostMapping("/requests/{id}/accept")
    public ResponseEntity<TripRequest> acceptTrip(@AuthenticationPrincipal User user,
                                                  @PathVariable Long id) {
        return ResponseEntity.ok(tripService.acceptTrip(user, id));
    }

    @PostMapping("/requests/{id}/start")
    public ResponseEntity<TripRequest> startTrip(@AuthenticationPrincipal User user,
                                                 @PathVariable Long id) {
        return ResponseEntity.ok(tripService.startTrip(user, id));
    }

    @PostMapping("/requests/{id}/complete")
    public ResponseEntity<TripRequest> completeTrip(@AuthenticationPrincipal User user,
                                                    @PathVariable Long id) {
        return ResponseEntity.ok(tripService.completeTrip(user, id));
    }
}
