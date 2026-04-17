package com.driverapp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "trip_requests")
@Getter
@Setter
@NoArgsConstructor
public class TripRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "passenger_id", nullable = false)
    @JsonIgnoreProperties({"passwordHash", "fcmToken", "driverProfile", "admin"})
    private User passenger;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id")
    @JsonIgnoreProperties({"passwordHash", "fcmToken", "driverProfile", "admin"})
    private User driver;

    @Column(nullable = false)
    private String pickupAddress;

    @Column(nullable = false)
    private String destinationAddress;

    @Column(nullable = false)
    private String zone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TripStatus status = TripStatus.REQUESTED;

    @Column(nullable = false)
    private LocalDateTime requestedAt = LocalDateTime.now();

    @Column
    private LocalDateTime acceptedAt;

    @Column
    private LocalDateTime completedAt;
}
