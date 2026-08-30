package com.pulsebysolutions.onlinebookstoreinternshipproject.controller;

import com.pulsebysolutions.onlinebookstoreinternshipproject.dto.request.RegisterRequest;
import com.pulsebysolutions.onlinebookstoreinternshipproject.dto.response.UserResponse;
import com.pulsebysolutions.onlinebookstoreinternshipproject.entity.User;
import com.pulsebysolutions.onlinebookstoreinternshipproject.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admins")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllAdmins() {
        return ResponseEntity.ok(adminService.getAllAdmins());
    }

    @PostMapping
    public ResponseEntity<UserResponse> createAdmin(
            @Valid @RequestBody RegisterRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(adminService.createAdmin(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAdmin(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {

        adminService.deleteAdmin(id, currentUser);

        return ResponseEntity.noContent().build();
    }
}