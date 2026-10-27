package com.fixtrack.inventory.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fixtrack.inventory.entity.PartRequestStatus;

// suggestedSupplier es el proveedor preferido del repuesto mientras la solicitud no se haya pedido
public record PartRequestResponse(
		Long id,
		Long interventionId,
		String interventionCode,
		PartRequestStatus status,
		Long sparePartId,
		String sparePartName,
		String sku,
		String description,
		int quantity,
		BigDecimal estimatedCost,
		BigDecimal actualCost,
		Long supplierId,
		String supplierName,
		SuggestedSupplierResponse suggestedSupplier,
		String requestedByName,
		String handledByName,
		String rejectionReason,
		LocalDateTime requestedAt,
		LocalDateTime receivedAt) {
}
