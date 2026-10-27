package com.fixtrack.inventory.dto.report;

import java.math.BigDecimal;

import com.fixtrack.inventory.entity.PartQuality;

// Fila tal como sale de la consulta; costValue = stock × costo, calculado en la base de datos
public record InventoryReportRow(Long id, String sku, String name, String partTypeName, String brandName,
		String deviceModelName, PartQuality quality, Integer stock, Integer minStock, BigDecimal costPrice,
		BigDecimal costValue) {
}
