package com.pulsebysolutions.onlinebookstoreinternshipproject.dto.request;

import com.pulsebysolutions.onlinebookstoreinternshipproject.entity.User;

public record RegisterRequest(String email, String phone, String password,String confirmPassword) {
    public static User toUser(RegisterRequest request) {
        return User.builder()
                .email(request.email())
                .phone(request.phone())
                .build();
    }
}