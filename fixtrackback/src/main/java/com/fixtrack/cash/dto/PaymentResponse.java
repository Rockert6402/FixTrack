package com.fixtrack.cash.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fixtrack.cash.entity.PaymentMethod;
import com.fixtrack.cash.entity.PaymentType;

public record PaymentResponse(
		Long id,
		PaymentType type,
		PaymentMethod method,
		BigDecimal amount,
		Long receivedById,
		String receivedByName,
		LocalDateTime createdAt) {
}
