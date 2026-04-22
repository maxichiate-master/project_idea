package com.driverapp.service;

import com.driverapp.dto.RatingRequest;
import com.driverapp.dto.TripCreateRequest;
import com.driverapp.model.*;
import com.driverapp.repository.DriverProfileRepository;
import com.driverapp.repository.RatingRepository;
import com.driverapp.repository.TripRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TripService {

    private final TripRequestRepository tripRequestRepository;
    private final DriverProfileRepository driverProfileRepository;
    private final RatingRepository ratingRepository;
    private final FcmService fcmService;

    private static final List<TripStatus> ACTIVE_STATUSES =
            List.of(TripStatus.REQUESTED, TripStatus.ACCEPTED, TripStatus.IN_PROGRESS);

    public TripRequest createRequest(User passenger, TripCreateRequest req) {
        tripRequestRepository.findFirstByPassengerAndStatusIn(passenger, ACTIVE_STATUSES)
                .ifPresent(t -> { throw new ResponseStatusException(HttpStatus.CONFLICT, "You already have an active trip"); });

        TripRequest trip = new TripRequest();
        trip.setPassenger(passenger);
        trip.setPickupAddress(req.getPickupAddress());
        trip.setDestinationAddress(req.getDestinationAddress());
        trip.setZone(req.getZone());
        trip = tripRequestRepository.save(trip);

        List<DriverProfile> available = driverProfileRepository
                .findByStatusAndOnlineAndCurrentZone(DriverStatus.APPROVED, true, req.getZone());
        for (DriverProfile dp : available) {
            fcmService.sendNewTripNotification(dp.getUser(), trip);
        }

        return trip;
    }

    public TripRequest acceptTrip(User driver, Long tripId) {
        TripRequest trip = getTripOrThrow(tripId);

        if (trip.getStatus() != TripStatus.REQUESTED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Trip is no longer available");
        }

        tripRequestRepository.findFirstByDriverAndStatusIn(driver, List.of(TripStatus.ACCEPTED, TripStatus.IN_PROGRESS))
                .ifPresent(t -> { throw new ResponseStatusException(HttpStatus.CONFLICT, "You already have an active trip"); });

        trip.setDriver(driver);
        trip.setStatus(TripStatus.ACCEPTED);
        trip.setAcceptedAt(LocalDateTime.now());
        trip = tripRequestRepository.save(trip);

        fcmService.sendTripAcceptedNotification(trip.getPassenger(), trip, driver);
        return trip;
    }

    public TripRequest startTrip(User driver, Long tripId) {
        TripRequest trip = getTripOrThrow(tripId);
        validateDriverOwnsTrip(driver, trip);

        if (trip.getStatus() != TripStatus.ACCEPTED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Trip must be accepted before starting");
        }

        trip.setStatus(TripStatus.IN_PROGRESS);
        return tripRequestRepository.save(trip);
    }

    public TripRequest completeTrip(User driver, Long tripId) {
        TripRequest trip = getTripOrThrow(tripId);
        validateDriverOwnsTrip(driver, trip);

        if (trip.getStatus() != TripStatus.IN_PROGRESS) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Trip must be in progress to complete");
        }

        trip.setStatus(TripStatus.COMPLETED);
        trip.setCompletedAt(LocalDateTime.now());
        trip = tripRequestRepository.save(trip);

        fcmService.sendTripCompletedNotification(trip.getPassenger());
        return trip;
    }

    public TripRequest cancelTrip(User user, Long tripId) {
        TripRequest trip = getTripOrThrow(tripId);

        boolean isPassenger = trip.getPassenger().getId().equals(user.getId());
        boolean isDriver = trip.getDriver() != null && trip.getDriver().getId().equals(user.getId());

        if (!isPassenger && !isDriver) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your trip");
        }
        if (trip.getStatus() == TripStatus.COMPLETED || trip.getStatus() == TripStatus.CANCELLED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot cancel a finished trip");
        }

        trip.setStatus(TripStatus.CANCELLED);
        return tripRequestRepository.save(trip);
    }

    public Rating rateTrip(User rater, RatingRequest req) {
        TripRequest trip = getTripOrThrow(req.getTripId());

        if (trip.getStatus() != TripStatus.COMPLETED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can only rate completed trips");
        }
        if (ratingRepository.existsByTripAndRater(trip, rater)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Already rated this trip");
        }

        User rated;
        if (rater.getId().equals(trip.getPassenger().getId())) {
            rated = trip.getDriver();
        } else if (trip.getDriver() != null && rater.getId().equals(trip.getDriver().getId())) {
            rated = trip.getPassenger();
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your trip");
        }

        Rating rating = new Rating();
        rating.setTrip(trip);
        rating.setRater(rater);
        rating.setRated(rated);
        rating.setScore(req.getScore());
        rating.setComment(req.getComment());
        return ratingRepository.save(rating);
    }

    public TripRequest getActiveTrip(User user) {
        return tripRequestRepository.findFirstByPassengerAndStatusIn(user, ACTIVE_STATUSES)
                .or(() -> tripRequestRepository.findFirstByDriverAndStatusIn(user, List.of(TripStatus.ACCEPTED, TripStatus.IN_PROGRESS)))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No active trip"));
    }

    public List<TripRequest> getAvailableTripsForDriver(User driver) {
        DriverProfile profile = driverProfileRepository.findByUser(driver)
                .filter(p -> p.getStatus() == DriverStatus.APPROVED && p.isOnline())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Must be online as an approved driver"));

        return tripRequestRepository.findByZoneAndStatus(profile.getCurrentZone(), TripStatus.REQUESTED);
    }

    private TripRequest getTripOrThrow(Long id) {
        return tripRequestRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Trip not found"));
    }

    private void validateDriverOwnsTrip(User driver, TripRequest trip) {
        if (trip.getDriver() == null || !trip.getDriver().getId().equals(driver.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your trip");
        }
    }
}
