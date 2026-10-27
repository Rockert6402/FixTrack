package com.fixtrack.person.dto;

import com.fixtrack.person.entity.Availability;

import jakarta.validation.constraints.NotNull;

public record AvailabilityRequest(@NotNull Availability availability) {
}
