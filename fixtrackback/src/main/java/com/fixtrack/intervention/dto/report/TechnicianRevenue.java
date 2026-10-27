package com.fixtrack.intervention.dto.report;

import java.math.BigDecimal;

public record TechnicianRevenue(Long technicianId, String technicianName, Long orders, BigDecimal revenue,
		Long warranties) {
}
