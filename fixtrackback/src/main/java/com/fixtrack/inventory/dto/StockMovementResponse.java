package com.fixtrack.inventory.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fixtrack.inventory.entity.StockMovementType;

// Movimiento del kárdex; interventionCode y partRequestId llegan cuando el movimiento viene de una orden o una compra
public record StockMovementResponse(
		Long id,
		Long sparePartId,
		String sparePartName,
		String sku,
		StockMovementType type,
		int quantity,
		int balanceAfter,
		BigDecimal unitCost,
		String reason,
		Long interventionId,
		String interventionCode,
		Long partRequestId,
		Long createdById,
		String createdByName,
		LocalDateTime createdAt) {
}
