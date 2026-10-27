package com.fixtrack.intervention.dto.report;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderProfit(Long id, String code, LocalDateTime deliveredAt, String customerName, Long technicianId,
		String technicianName, boolean warranty, BigDecimal revenue, BigDecimal partsCost, BigDecimal profit) {
}
