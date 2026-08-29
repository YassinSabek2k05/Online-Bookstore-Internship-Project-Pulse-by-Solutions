package com.pulsebysolutions.onlinebookstoreinternshipproject.dto.request;

public record CreateAdminRequest(
        String email,
        String password,
        String phone
) {
}