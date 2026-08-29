package com.pulsebysolutions.onlinebookstoreinternshipproject.service;

import com.pulsebysolutions.onlinebookstoreinternshipproject.dto.request.CreateAdminRequest;
import com.pulsebysolutions.onlinebookstoreinternshipproject.dto.response.AdminResponse;
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

    public List<AdminResponse> getAllAdmins() {

        return userRepository.findAllByRole(Role.ADMIN)
                .stream()
                .map(AdminResponse::fromUser)
                .toList();
    }

    public AdminResponse createAdmin(CreateAdminRequest request) {

        if (userRepository.getByEmail(request.email()) != null) {
            throw new DuplicateResourceException("Email already exists");
        }

        User admin = new User();

        admin.setEmail(request.email());
        admin.setPassword(passwordEncoder.encode(request.password()));
        admin.setPhone(request.phone());
        admin.setRole(Role.ADMIN);

        User savedAdmin = userRepository.save(admin);

        return AdminResponse.fromUser(savedAdmin);
    }

    public void deleteAdmin(Long id) {

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