package com.pulsebysolutions.onlinebookstoreinternshipproject.service;

import com.pulsebysolutions.onlinebookstoreinternshipproject.dto.request.RegisterRequest;
import com.pulsebysolutions.onlinebookstoreinternshipproject.dto.response.UserResponse;
import com.pulsebysolutions.onlinebookstoreinternshipproject.entity.User;
import com.pulsebysolutions.onlinebookstoreinternshipproject.entity.User.Role;
import com.pulsebysolutions.onlinebookstoreinternshipproject.exception.DuplicateResourceException;
import com.pulsebysolutions.onlinebookstoreinternshipproject.exception.ResourceNotFoundException;
import com.pulsebysolutions.onlinebookstoreinternshipproject.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminService(UserRepository userRepository,
                        PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserResponse> getAllAdmins() {

        return userRepository.findAllByRole(Role.ADMIN)
                .stream()
                .map(UserResponse::fromUser)
                .toList();
    }

    public UserResponse createAdmin(RegisterRequest request) {

        if (userRepository.getByEmail(request.email()) != null) {
            throw new DuplicateResourceException("Email already exists");
        }

        // The request carries no role field at all, so ADMIN can only ever be
        // assigned here on the server (spec §7.4).
        User admin = RegisterRequest.toUser(request);

        admin.setPassword(passwordEncoder.encode(request.password()));
        admin.setRole(Role.ADMIN);

        User savedAdmin = userRepository.save(admin);

        return UserResponse.fromUser(savedAdmin);
    }

    public void deleteAdmin(Long id, User currentUser) {

        // Without this an admin can delete themselves, and deleting the last
        // admin would leave nobody able to administer the store.
        if (currentUser.getId().equals(id)) {
            throw new IllegalArgumentException(
                    "You cannot delete your own admin account"
            );
        }

        User admin = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Admin not found with id: " + id
                        ));

        if (admin.getRole() != Role.ADMIN) {
            throw new ResourceNotFoundException(
                    "Admin not found with id: " + id
            );
        }

        userRepository.delete(admin);
    }
}