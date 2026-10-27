package com.fixtrack.intervention.dto;

import com.fixtrack.intervention.entity.InterventionStatus;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record StatusChangeRequest(@NotNull InterventionStatus status, @Size(max = 500) String note) {
}
