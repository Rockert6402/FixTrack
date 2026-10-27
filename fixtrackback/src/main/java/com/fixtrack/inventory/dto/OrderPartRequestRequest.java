package com.fixtrack.inventory.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;

// supplierId null usa el proveedor preferido del repuesto
public record OrderPartRequestRequest(
		Long supplierId,
		@DecimalMin("0.00") @Digits(integer = 10, fraction = 2) BigDecimal estimatedCost) {
}
