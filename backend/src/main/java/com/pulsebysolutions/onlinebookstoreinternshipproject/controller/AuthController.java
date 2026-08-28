package com.pulsebysolutions.onlinebookstoreinternshipproject.controller;

import com.pulsebysolutions.onlinebookstoreinternshipproject.dto.request.LoginRequest;
import com.pulsebysolutions.onlinebookstoreinternshipproject.dto.request.RegisterRequest;
import com.pulsebysolutions.onlinebookstoreinternshipproject.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    public AuthController(AuthService authService) {
        this.authService = authService;
    }
    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        response.addCookie(authService.login(request));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.status(201).build();
    }


}
