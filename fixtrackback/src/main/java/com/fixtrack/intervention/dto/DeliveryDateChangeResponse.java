package com.fixtrack.intervention.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record DeliveryDateChangeResponse(Long id, LocalDate previousDate, LocalDate newDate, String reason,
		PersonRef changedBy, LocalDateTime changedAt) {
}
