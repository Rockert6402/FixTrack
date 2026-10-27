package com.fixtrack.inventory.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

// Repuesto del catálogo (sparePartId) o descrito a mano (description); con catálogo la descripción es opcional
public record CreatePartRequestRequest(
		Long sparePartId,
		@Size(max = 500) String description,
		@NotNull @Positive @Max(1000) Integer quantity,
		@DecimalMin("0.00") @Digits(integer = 10, fraction = 2) BigDecimal estimatedCost) {
}
