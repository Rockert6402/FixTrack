package com.fixtrack.intervention.dto.report;

import java.math.BigDecimal;

// Ganancia = total cobrado − costo de repuestos; warrantyCost es el costo de repuestos de las garantías (ya incluido)
public record ProfitTotals(long orders, long warranties, BigDecimal revenue, BigDecimal partsCost,
		BigDecimal warrantyCost, BigDecimal profit) {
}
