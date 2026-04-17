package com.driverapp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "ratings")
@Getter
@Setter
@NoArgsConstructor
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id", nullable = false)
    @JsonIgnoreProperties({"passenger", "driver"})
    private TripRequest trip;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rater_id", nullable = false)
    @JsonIgnoreProperties({"passwordHash", "fcmToken", "driverProfile", "admin"})
    private User rater;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rated_id", nullable = false)
    @JsonIgnoreProperties({"passwordHash", "fcmToken", "driverProfile", "admin"})
    private User rated;

    @Column(nullable = false)
    private int score;

    @Column
    private String comment;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
