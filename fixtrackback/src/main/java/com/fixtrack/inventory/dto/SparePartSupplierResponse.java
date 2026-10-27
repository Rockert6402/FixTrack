package com.fixtrack.inventory.dto;

import java.math.BigDecimal;

public record SparePartSupplierResponse(
		Long supplierId,
		String supplierName,
		String taxId,
		boolean supplierActive,
		String supplierSku,
		BigDecimal lastCost,
		Integer leadTimeDays,
		boolean preferred) {
}
