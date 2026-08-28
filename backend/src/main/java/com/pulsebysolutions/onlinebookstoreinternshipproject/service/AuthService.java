package com.pulsebysolutions.onlinebookstoreinternshipproject.service;

import com.pulsebysolutions.onlinebookstoreinternshipproject.dto.request.LoginRequest;
import com.pulsebysolutions.onlinebookstoreinternshipproject.dto.request.RegisterRequest;
import com.pulsebysolutions.onlinebookstoreinternshipproject.entity.User;
import com.pulsebysolutions.onlinebookstoreinternshipproject.repository.UserRepository;
import com.pulsebysolutions.onlinebookstoreinternshipproject.security.JWTService;
import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final JWTService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    @Value("${jwt_cookie.value.staff_time}")
    private long jwtExpirationInMs;

    public AuthService(JWTService jwtService, PasswordEncoder passwordEncoder, UserRepository userRepository, AuthenticationManager authenticationManager) {
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
    }

    public Cookie login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(),request.password())
        );
        String token = jwtService.generateToken(request.email(), jwtExpirationInMs);

        Cookie cookie = new Cookie("user_token", token);
        cookie.setHttpOnly(true);   // JS cannot read this
        cookie.setSecure(true);     // HTTPS only
        cookie.setPath("/");
        cookie.setAttribute("SameSite", "None");
        cookie.setMaxAge((int) (jwtExpirationInMs/60)); // 24 hours
        return cookie;
    }

    public void register(RegisterRequest request) {
        // Implement registration logic here
        validate(request);
        User user = RegisterRequest.toUser(request);
        String encodedPassword = passwordEncoder.encode(request.password());
        user.setPassword(encodedPassword);
        userRepository.save(user);
    }

    private void validate(RegisterRequest request) {
        /*
         * 7.6 Backend Validation
         * • Backend validation is required even when frontend validation is implemented.
         * • Email must be valid and unique.
         * • Password must satisfy the required minimum length.
         * • Confirm Password must match Password during registration.
         * • Phone number must have a valid format.
         * • Book title, author, and category cannot be empty.
         * • Book price must be greater than zero.
         */
    }
}
