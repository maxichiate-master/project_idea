package com.driverapp.service;

import com.driverapp.model.TripRequest;
import com.driverapp.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FcmService {

    public void sendNewTripNotification(User driver, TripRequest trip) {
        if (driver.getFcmToken() == null) return;
        log.info("FCM [NEW_TRIP] -> driver {} | trip {} in zone {}", driver.getId(), trip.getId(), trip.getZone());
        // TODO: wire up Firebase Admin SDK
    }

    public void sendTripAcceptedNotification(User passenger, TripRequest trip, User driver) {
        if (passenger.getFcmToken() == null) return;
        log.info("FCM [TRIP_ACCEPTED] -> passenger {} | driver {}", passenger.getId(), driver.getId());
        // TODO: wire up Firebase Admin SDK
    }

    public void sendTripCompletedNotification(User passenger) {
        if (passenger.getFcmToken() == null) return;
        log.info("FCM [TRIP_COMPLETED] -> passenger {}", passenger.getId());
        // TODO: wire up Firebase Admin SDK
    }
}
