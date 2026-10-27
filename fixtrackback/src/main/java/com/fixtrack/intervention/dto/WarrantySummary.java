package com.fixtrack.intervention.dto;

import java.time.LocalDateTime;

import com.fixtrack.intervention.entity.InterventionStatus;

public record WarrantySummary(Long id, String code, InterventionStatus status, LocalDateTime receivedAt,
		LocalDateTime deliveredAt) {
}
