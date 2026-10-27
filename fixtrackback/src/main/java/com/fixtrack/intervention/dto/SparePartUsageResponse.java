package com.fixtrack.intervention.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fixtrack.inventory.entity.PartQuality;

public record SparePartUsageResponse(
		Long id,
		Long sparePartId,
		String sparePartName,
		String sku,
		PartQuality quality,
		int quantity,
		BigDecimal unitPrice,
		BigDecimal subtotal,
		PersonRef registeredBy,
		LocalDateTime createdAt) {
}
