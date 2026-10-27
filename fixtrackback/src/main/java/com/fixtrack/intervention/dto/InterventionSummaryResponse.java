package com.fixtrack.intervention.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fixtrack.intervention.entity.InterventionStatus;

public record InterventionSummaryResponse(
		Long id,
		String code,
		InterventionStatus status,
		boolean overdue,
		String customerName,
		String customerDocument,
		String device,
		String technicianName,
		LocalDate estimatedDeliveryDate,
		LocalDateTime receivedAt) {
}
