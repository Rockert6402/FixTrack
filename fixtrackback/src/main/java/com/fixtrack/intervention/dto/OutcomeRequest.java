package com.fixtrack.intervention.dto;

import java.math.BigDecimal;

import com.fixtrack.intervention.entity.RepairOutcome;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

// warrantyDays null usa DEFAULT_WARRANTY_DAYS; diagnosisFee es la mano de obra cuando no se repara
public record OutcomeRequest(
		@NotNull RepairOutcome outcome,
		@Size(max = 500) String reason,
		@PositiveOrZero @Max(3650) Integer warrantyDays,
		@DecimalMin("0.00") @Digits(integer = 10, fraction = 2) BigDecimal diagnosisFee) {
}
