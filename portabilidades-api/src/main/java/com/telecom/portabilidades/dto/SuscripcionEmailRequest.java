package com.telecom.portabilidades.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SuscripcionEmailRequest(
        @NotBlank @Email @Size(max = 254) String email
) {
}
