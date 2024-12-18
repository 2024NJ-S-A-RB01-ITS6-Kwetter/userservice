package s_a_rb01_its6.userservice.domain.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public record UserRequest(
        @NotBlank(message = "ID cannot be blank")
        String id,

        @Email(message = "Please provide a valid email address")
        @NotBlank(message = "Email cannot be blank")
        String email,

        @NotBlank(message = "Username cannot be blank")
        @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
        String username,

        @NotBlank(message = "Password cannot be blank")
        @Size(min = 8, message = "Password must be at least 8 characters long")
        String password,

        @Size(max = 200, message = "Bio must not exceed 200 characters")
        String bio
) {}