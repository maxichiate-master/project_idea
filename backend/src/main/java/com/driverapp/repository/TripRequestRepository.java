package com.driverapp.repository;

import com.driverapp.model.TripRequest;
import com.driverapp.model.TripStatus;
import com.driverapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface TripRequestRepository extends JpaRepository<TripRequest, Long> {
    Optional<TripRequest> findFirstByPassengerAndStatusIn(User passenger, Collection<TripStatus> statuses);
    Optional<TripRequest> findFirstByDriverAndStatusIn(User driver, Collection<TripStatus> statuses);
    List<TripRequest> findByZoneAndStatus(String zone, TripStatus status);
}
