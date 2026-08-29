package com.pulsebysolutions.onlinebookstoreinternshipproject.service;

import com.pulsebysolutions.onlinebookstoreinternshipproject.dto.response.UserResponse;
import com.pulsebysolutions.onlinebookstoreinternshipproject.entity.User;
import com.pulsebysolutions.onlinebookstoreinternshipproject.exception.ResourceNotFoundException;
import com.pulsebysolutions.onlinebookstoreinternshipproject.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDetails getByEmail(String email) {
        return userRepository.getByEmail(email);
    }

//    public UserResponse getCurrentUser() {
//
//        Authentication authentication =
//                SecurityContextHolder.getContext().getAuthentication();
//
//        String email = authentication.getName();
//
//        User user = userRepository.getByEmail(email);
//
//        if (user == null) {
//            throw new ResourceNotFoundException(
//                    "User not found with email: " + email
//            );
//        }
//
//        return UserResponse.fromUser(user);
//    }

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        UserDetails user = userRepository.getByEmail(email);

        if (user == null) {
            throw new UsernameNotFoundException(
                    "No user found with email: " + email
            );
        }

        return user;
    }
}