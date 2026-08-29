package com.pulsebysolutions.onlinebookstoreinternshipproject.dto.response;

import com.pulsebysolutions.onlinebookstoreinternshipproject.entity.User;
import com.pulsebysolutions.onlinebookstoreinternshipproject.entity.User.Role;
public record AdminResponse(
        Long id,
        String email,
        String phone,
        Role role
) {

    public static AdminResponse fromUser(User user) {
        return new AdminResponse(
                user.getId(),
                user.getEmail(),
                user.getPhone(),
                user.getRole()
        );
    }
}