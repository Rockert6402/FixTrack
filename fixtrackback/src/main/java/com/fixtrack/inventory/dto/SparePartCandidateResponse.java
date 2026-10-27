package com.fixtrack.inventory.dto;

import java.math.BigDecimal;

import com.fixtrack.inventory.entity.PartQuality;

// Repuesto ofrecido al técnico para una orden; compatible indica si sirve para el modelo del equipo
public record SparePartCandidateResponse(
		Long id,
		String name,
		String sku,
		String partTypeName,
		String deviceModelName,
		String brandName,
		PartQuality quality,
		int stock,
		BigDecimal salePrice,
		boolean compatible) {
}
