package com.fixtrack.inventory.dto;

import java.math.BigDecimal;

import com.fixtrack.inventory.entity.PartQuality;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

// Edición parcial: los campos que llegan en null conservan su valor
public record UpdateSparePartRequest(
		@Size(max = 150) @Pattern(regexp = ".*\\S.*", message = "no debe estar en blanco") String name,
		@Size(max = 500) String description,
		@Size(max = 50) @Pattern(regexp = ".*\\S.*", message = "no debe estar en blanco") String sku,
		Long partTypeId,
		Long deviceModelId,
		PartQuality quality,
		@PositiveOrZero Integer minStock,
		@DecimalMin("0.00") @Digits(integer = 10, fraction = 2) BigDecimal costPrice,
		@DecimalMin("0.00") @Digits(integer = 10, fraction = 2) BigDecimal salePrice) {
}
