package com.fixtrack.intervention.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record SparePartUsageRequest(@NotNull Long sparePartId, @NotNull @Positive Integer quantity) {
}
