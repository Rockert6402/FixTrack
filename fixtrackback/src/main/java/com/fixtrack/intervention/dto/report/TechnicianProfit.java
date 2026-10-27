package com.fixtrack.intervention.dto.report;

import java.math.BigDecimal;

public record TechnicianProfit(Long technicianId, String technicianName, long orders, long warranties,
		BigDecimal revenue, BigDecimal partsCost, BigDecimal warrantyCost, BigDecimal profit) {
}
