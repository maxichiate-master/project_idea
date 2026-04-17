package com.driverapp.repository;

import com.driverapp.model.DriverProfile;
import com.driverapp.model.DriverStatus;
import com.driverapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DriverProfileRepository extends JpaRepository<DriverProfile, Long> {
    Optional<DriverProfile> findByUser(User user);
    List<DriverProfile> findByStatus(DriverStatus status);
    List<DriverProfile> findByStatusAndOnlineAndCurrentZone(DriverStatus status, boolean online, String zone);
}
