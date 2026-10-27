package com.fixtrack.intervention.dto;

import java.time.LocalDateTime;

import com.fixtrack.intervention.entity.InterventionStatus;

public record StatusHistoryResponse(Long id, InterventionStatus fromStatus, InterventionStatus toStatus,
		PersonRef changedBy, String note, LocalDateTime changedAt) {
}
