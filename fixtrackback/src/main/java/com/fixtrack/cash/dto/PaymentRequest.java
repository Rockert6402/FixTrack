package com.fixtrack.cash.dto;

import java.math.BigDecimal;

import com.fixtrack.cash.entity.PaymentMethod;
import com.fixtrack.cash.entity.PaymentType;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

public record PaymentRequest(
		@NotNull PaymentType type,
		@NotNull PaymentMethod method,
		@NotNull @DecimalMin("0.01") @Digits(integer = 10, fraction = 2) BigDecimal amount) {
}
