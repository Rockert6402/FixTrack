package com.fixtrack.inventory.dto;

import java.math.BigDecimal;

import com.fixtrack.device.entity.DeviceType;
import com.fixtrack.inventory.entity.PartQuality;
import com.fixtrack.inventory.entity.StockStatus;

public record SparePartResponse(
		Long id,
		String name,
		String description,
		String sku,
		Long partTypeId,
		String partTypeName,
		Long deviceModelId,
		String deviceModelName,
		String brandName,
		DeviceType deviceType,
		PartQuality quality,
		int stock,
		int minStock,
		StockStatus stockStatus,
		BigDecimal costPrice,
		BigDecimal salePrice) {
}
