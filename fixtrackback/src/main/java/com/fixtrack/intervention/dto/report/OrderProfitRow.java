package com.fixtrack.intervention.dto.report;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// Fila de la consulta: lo cobrado y el costo de repuestos de cada orden entregada
public record OrderProfitRow(Long id, Long warrantyOfId, Integer warrantyNumber, LocalDateTime deliveredAt,
		String customerName, Long technicianId, String technicianName, BigDecimal revenue, BigDecimal partsCost) {
}
