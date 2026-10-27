package com.fixtrack.intervention.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

public record LaborCostRequest(@NotNull @DecimalMin("0.00") @Digits(integer = 10, fraction = 2) BigDecimal laborCost) {
}
