package com.fixtrack.intervention.dto;

import java.math.BigDecimal;
import java.util.List;

// Repuestos usados en la orden y el total: mano de obra + cantidad × precio de venta guardado.
// partsCost (cantidad × costo guardado) y profit (total − costo) no llegan al asesor
public record InterventionPartsResponse(
		List<SparePartUsageResponse> items,
		BigDecimal partsTotal,
		BigDecimal laborCost,
		BigDecimal total,
		BigDecimal partsCost,
		BigDecimal profit) {
}
