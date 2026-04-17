package com.driverapp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "driver_profiles")
@Getter
@Setter
@NoArgsConstructor
public class DriverProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"passwordHash", "fcmToken", "driverProfile", "admin"})
    private User user;

    @Column(nullable = false)
    private String dni;

    @Column(nullable = false)
    private String licenseNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DriverStatus status = DriverStatus.PENDING;

    @Column(nullable = false)
    private boolean online = false;

    @Column
    private String currentZone;
}
