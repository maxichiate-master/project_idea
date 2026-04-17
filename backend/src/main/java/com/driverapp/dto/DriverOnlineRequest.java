package com.driverapp.dto;

import lombok.Data;

@Data
public class DriverOnlineRequest {
    private boolean online;
    private String zone;
}
