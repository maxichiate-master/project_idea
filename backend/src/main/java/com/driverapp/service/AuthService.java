package com.driverapp.service;

import com.driverapp.dto.AuthResponse;
import com.driverapp.dto.LoginRequest;
import com.driverapp.dto.RegisterRequest;
import com.driverapp.model.DriverStatus;
import com.driverapp.model.User;
import com.driverapp.repository.UserRepository;
import com.driverapp.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
        }
        if (userRepository.existsByPhone(req.getPhone())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Phone already in use");
        }

        User user = new User();
        user.setName(req.getName());
        user.setEmail(req.getEmail());
        user.setPhone(req.getPhone());
        user.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        userRepository.save(user);

        return buildAuthResponse(user);
    }

    public AuthResponse login(LoginRequest req) {
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        return buildAuthResponse(user);
    }

    public void updateFcmToken(User user, String fcmToken) {
        user.setFcmToken(fcmToken);
        userRepository.save(user);
    }

    private AuthResponse buildAuthResponse(User user) {
        AuthResponse response = new AuthResponse();
        response.setToken(jwtUtil.generateToken(user.getEmail()));
        response.setUserId(user.getId());
        response.setName(user.getName());
        response.setDriver(user.getDriverProfile() != null &&
                user.getDriverProfile().getStatus() == DriverStatus.APPROVED);
        return response;
    }
}
