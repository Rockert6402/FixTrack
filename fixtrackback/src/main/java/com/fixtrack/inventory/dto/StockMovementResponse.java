package com.fixtrack.inventory.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fixtrack.inventory.entity.StockMovementType;

public record StockMovementResponse(
		Long id,
		StockMovementType type,
		int quantity,
		int balanceAfter,
		BigDecimal unitCost,
		String reason,
		Long createdById,
		String createdByName,
		LocalDateTime createdAt) {
}
