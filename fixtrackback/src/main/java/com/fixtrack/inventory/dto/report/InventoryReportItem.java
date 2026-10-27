package com.fixtrack.inventory.dto.report;

import java.math.BigDecimal;

import com.fixtrack.inventory.entity.PartQuality;
import com.fixtrack.inventory.entity.StockStatus;

public record InventoryReportItem(Long id, String sku, String name, String partTypeName, String deviceModel,
		PartQuality quality, int stock, int minStock, StockStatus stockStatus, BigDecimal costPrice,
		BigDecimal costValue) {
}
