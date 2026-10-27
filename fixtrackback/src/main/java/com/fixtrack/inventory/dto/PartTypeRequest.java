package com.fixtrack.inventory.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PartTypeRequest(@NotBlank @Size(max = 100) String name) {
}
