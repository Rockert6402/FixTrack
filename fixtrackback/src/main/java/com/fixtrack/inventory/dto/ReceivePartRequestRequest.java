package com.fixtrack.inventory.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

// actualCost es el costo real por unidad; sparePartId asocia el repuesto si la solicitud se describió a mano
public record ReceivePartRequestRequest(
		@NotNull @DecimalMin("0.00") @Digits(integer = 10, fraction = 2) BigDecimal actualCost,
		Long sparePartId) {
}
