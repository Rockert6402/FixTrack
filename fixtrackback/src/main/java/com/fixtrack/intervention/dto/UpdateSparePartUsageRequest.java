package com.fixtrack.intervention.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record UpdateSparePartUsageRequest(@NotNull @Positive Integer quantity) {
}
