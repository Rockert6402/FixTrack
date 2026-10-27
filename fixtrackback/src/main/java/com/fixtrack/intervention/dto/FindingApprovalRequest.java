package com.fixtrack.intervention.dto;

import com.fixtrack.intervention.entity.ApprovalStatus;

import jakarta.validation.constraints.NotNull;

// Respuesta del cliente a un daño adicional: APPROVED o REJECTED
public record FindingApprovalRequest(@NotNull ApprovalStatus decision) {
}
