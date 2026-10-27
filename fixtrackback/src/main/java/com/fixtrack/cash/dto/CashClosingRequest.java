package com.fixtrack.cash.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CashClosingRequest(
		@NotNull @DecimalMin("0.00") @Digits(integer = 10, fraction = 2) BigDecimal countedCash,
		@Size(max = 500) String notes) {
}
