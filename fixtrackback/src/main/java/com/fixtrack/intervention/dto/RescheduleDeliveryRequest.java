package com.fixtrack.intervention.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RescheduleDeliveryRequest(@NotNull LocalDate newDate, @NotBlank @Size(max = 500) String reason) {
}
