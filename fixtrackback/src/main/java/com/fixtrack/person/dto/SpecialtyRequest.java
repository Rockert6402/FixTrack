package com.fixtrack.person.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SpecialtyRequest(@NotBlank @Size(max = 100) String name) {
}
