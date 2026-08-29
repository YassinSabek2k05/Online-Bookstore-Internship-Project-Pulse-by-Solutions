package com.pulsebysolutions.onlinebookstoreinternshipproject.dto.response;

public record UserResponse(
    String email,
    String phone
) {
    public static UserResponse fromUser(com.pulsebysolutions.onlinebookstoreinternshipproject.entity.User user) {
        return new UserResponse(
            user.getEmail(),
            user.getPhone()
        );
    }
}
