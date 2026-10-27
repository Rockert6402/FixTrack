package com.fixtrack.inventory.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RejectPartRequestRequest(@NotBlank @Size(max = 500) String reason) {
}
