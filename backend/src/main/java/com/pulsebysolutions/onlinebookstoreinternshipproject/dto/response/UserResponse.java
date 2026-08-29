package com.pulsebysolutions.onlinebookstoreinternshipproject.dto.response;

import com.pulsebysolutions.onlinebookstoreinternshipproject.entity.User;
import com.pulsebysolutions.onlinebookstoreinternshipproject.entity.User.Role;

public record UserResponse(
        Long id,
        String email,
        String phone,
        Role role
) {

    public static UserResponse fromUser(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getPhone(),
                user.getRole()
        );
    }
}