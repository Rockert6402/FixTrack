package com.fixtrack.inventory.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

// preferred null cuenta como no preferido
public record SparePartSupplierRequest(
		@NotNull Long supplierId,
		@Size(max = 50) String supplierSku,
		@DecimalMin("0.00") @Digits(integer = 10, fraction = 2) BigDecimal lastCost,
		@PositiveOrZero @Max(365) Integer leadTimeDays,
		Boolean preferred) {
}
