package com.pulsebysolutions.onlinebookstoreinternshipproject.controller;

import com.pulsebysolutions.onlinebookstoreinternshipproject.dto.response.UserResponse;
import com.pulsebysolutions.onlinebookstoreinternshipproject.entity.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @GetMapping("/test")
    public String test() {
        return "User controller is working!";
    }

    @GetMapping("/me")
    public UserResponse getCurrentUser(@AuthenticationPrincipal User user) {
        return UserResponse.fromUser(user);
    }
}
