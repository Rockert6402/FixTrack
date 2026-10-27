package com.fixtrack.cash.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

// cashDifference = efectivo contado − efectivo esperado (negativo si falta dinero)
public record CashClosingResponse(
		Long id,
		LocalDate closingDate,
		Long closedById,
		String closedByName,
		BigDecimal expectedCash,
		BigDecimal expectedTransfer,
		BigDecimal expectedCard,
		BigDecimal expectedTotal,
		BigDecimal countedCash,
		BigDecimal cashDifference,
		String notes,
		long payments,
		LocalDateTime createdAt) {
}
