package com.fixtrack.intervention.dto;

import com.fixtrack.intervention.entity.ServiceType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AdditionalFindingRequest(@NotNull ServiceType serviceType, @NotBlank @Size(max = 1000) String description) {
}
