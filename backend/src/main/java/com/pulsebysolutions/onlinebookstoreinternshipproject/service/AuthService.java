package com.pulsebysolutions.onlinebookstoreinternshipproject.service;

import com.pulsebysolutions.onlinebookstoreinternshipproject.dto.request.LoginRequest;
import com.pulsebysolutions.onlinebookstoreinternshipproject.dto.request.RegisterRequest;
import com.pulsebysolutions.onlinebookstoreinternshipproject.entity.User;
import com.pulsebysolutions.onlinebookstoreinternshipproject.exception.DuplicateResourceException;
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

    public AuthService(
            JWTService jwtService,
            PasswordEncoder passwordEncoder,
            UserRepository userRepository,
            AuthenticationManager authenticationManager) {

        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
    }

    public Cookie login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        String token = jwtService.generateToken(
                request.email(),
                jwtExpirationInMs
        );

        Cookie cookie = new Cookie("user_token", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setAttribute("SameSite", "None");
        cookie.setMaxAge((int) (jwtExpirationInMs / 1000));

        return cookie;
    }

    public void register(RegisterRequest request) {

        validate(request);

        User user = RegisterRequest.toUser(request);

        String encodedPassword =
                passwordEncoder.encode(request.password());

        user.setPassword(encodedPassword);

        userRepository.save(user);
    }

    private void validate(RegisterRequest request) {

        if (userRepository.getByEmail(request.email()) != null) {
            throw new DuplicateResourceException(
                    "Email already exists"
            );
        }

        if (!request.password().equals(request.confirmPassword())) {
            throw new IllegalArgumentException(
                    "Confirm password must match password"
            );
        }
    }
}