package com.fixtrack.inventory.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record StockEntryRequest(
		@NotNull @Positive @Max(100_000) Integer quantity,
		@NotNull @DecimalMin("0.00") @Digits(integer = 10, fraction = 2) BigDecimal unitCost,
		@NotBlank @Size(max = 500) String reason) {
}
