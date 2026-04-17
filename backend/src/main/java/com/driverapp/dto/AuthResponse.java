package com.driverapp.dto;

import lombok.Data;

@Data
public class AuthResponse {
    private String token;
    private Long userId;
    private String name;
    private boolean driver;
}
