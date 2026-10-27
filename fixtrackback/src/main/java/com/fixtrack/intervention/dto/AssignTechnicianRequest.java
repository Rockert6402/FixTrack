package com.fixtrack.intervention.dto;

import jakarta.validation.constraints.NotNull;

public record AssignTechnicianRequest(@NotNull Long technicianId) {
}
