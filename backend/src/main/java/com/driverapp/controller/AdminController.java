package com.driverapp.controller;

import com.driverapp.model.DriverProfile;
import com.driverapp.service.DriverService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final DriverService driverService;

    @GetMapping("/drivers/pending")
    public ResponseEntity<List<DriverProfile>> getPending() {
        return ResponseEntity.ok(driverService.getPendingApplications());
    }

    @PutMapping("/drivers/{id}/approve")
    public ResponseEntity<DriverProfile> approve(@PathVariable Long id) {
        return ResponseEntity.ok(driverService.approve(id));
    }

    @PutMapping("/drivers/{id}/suspend")
    public ResponseEntity<DriverProfile> suspend(@PathVariable Long id) {
        return ResponseEntity.ok(driverService.suspend(id));
    }
}
