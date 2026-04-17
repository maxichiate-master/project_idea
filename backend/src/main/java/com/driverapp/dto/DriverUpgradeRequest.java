package com.driverapp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DriverUpgradeRequest {
    @NotBlank
    private String dni;

    @NotBlank
    private String licenseNumber;
}
