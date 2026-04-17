package com.driverapp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TripCreateRequest {
    @NotBlank
    private String pickupAddress;

    @NotBlank
    private String destinationAddress;

    @NotBlank
    private String zone;
}
