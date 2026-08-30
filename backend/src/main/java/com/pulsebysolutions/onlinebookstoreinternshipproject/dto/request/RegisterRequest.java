package com.pulsebysolutions.onlinebookstoreinternshipproject.dto.request;

import com.pulsebysolutions.onlinebookstoreinternshipproject.entity.User;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        String email,

        @NotBlank(message = "Phone number is required")
        @Pattern(
                regexp = "^\\+?[0-9]{10,15}$",
                message = "Phone number must be valid"
        )
        String phone,

        @NotBlank(message = "Password is required")
        @Size(
                min = 8,
                message = "Password must be at least 8 characters"
        )
        String password,

        @NotBlank(message = "Confirm password is required")
        String confirmPassword

) {

    /**
     * Looks uncalled, but @Valid invokes it reflectively: Bean Validation reads
     * isXxx()/getXxx() as properties, so the constraint must RETURN false rather
     * than throw, and the return type must stay boolean. A void return makes
     * Hibernate Validator abort the whole class with HV000132, which silently
     * disables every other rule on this record too.
     */
    @AssertTrue(message = "Confirm password must match password")
    public boolean isPasswordConfirmed() {
        return password != null && password.equals(confirmPassword);
    }

    public static User toUser(RegisterRequest request) {
        return User.builder()
                .email(request.email())
                .phone(request.phone())
                .build();
    }
}