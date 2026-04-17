package com.driverapp.service;

import com.driverapp.dto.DriverUpgradeRequest;
import com.driverapp.model.DriverProfile;
import com.driverapp.model.DriverStatus;
import com.driverapp.model.User;
import com.driverapp.repository.DriverProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DriverService {

    private final DriverProfileRepository driverProfileRepository;

    public DriverProfile applyForDriver(User user, DriverUpgradeRequest req) {
        if (driverProfileRepository.findByUser(user).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Driver application already submitted");
        }

        DriverProfile profile = new DriverProfile();
        profile.setUser(user);
        profile.setDni(req.getDni());
        profile.setLicenseNumber(req.getLicenseNumber());
        return driverProfileRepository.save(profile);
    }

    public DriverProfile setOnlineStatus(User user, boolean online, String zone) {
        DriverProfile profile = getApprovedProfile(user);
        profile.setOnline(online);
        if (online) {
            if (zone == null || zone.isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Zone is required when going online");
            }
            profile.setCurrentZone(zone);
        }
        return driverProfileRepository.save(profile);
    }

    public List<DriverProfile> getPendingApplications() {
        return driverProfileRepository.findByStatus(DriverStatus.PENDING);
    }

    public DriverProfile approve(Long profileId) {
        DriverProfile profile = driverProfileRepository.findById(profileId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Driver profile not found"));
        profile.setStatus(DriverStatus.APPROVED);
        return driverProfileRepository.save(profile);
    }

    public DriverProfile suspend(Long profileId) {
        DriverProfile profile = driverProfileRepository.findById(profileId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Driver profile not found"));
        profile.setStatus(DriverStatus.SUSPENDED);
        return driverProfileRepository.save(profile);
    }

    public DriverProfile getApprovedProfile(User user) {
        return driverProfileRepository.findByUser(user)
                .filter(p -> p.getStatus() == DriverStatus.APPROVED)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Not an approved driver"));
    }
}
