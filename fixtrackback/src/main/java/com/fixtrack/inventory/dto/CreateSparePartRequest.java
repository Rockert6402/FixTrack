package com.fixtrack.inventory.dto;

import java.math.BigDecimal;

import com.fixtrack.inventory.entity.PartQuality;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record CreateSparePartRequest(
		@NotBlank @Size(max = 150) String name,
		@Size(max = 500) String description,
		@NotBlank @Size(max = 50) String sku,
		@NotNull Long partTypeId,
		@NotNull Long deviceModelId,
		PartQuality quality,
		@PositiveOrZero Integer minStock,
		@NotNull @DecimalMin("0.00") @Digits(integer = 10, fraction = 2) BigDecimal costPrice,
		@NotNull @DecimalMin("0.00") @Digits(integer = 10, fraction = 2) BigDecimal salePrice) {
}
