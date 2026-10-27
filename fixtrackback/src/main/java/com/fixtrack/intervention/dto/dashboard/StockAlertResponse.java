package com.fixtrack.intervention.dto.dashboard;

import com.fixtrack.inventory.entity.StockStatus;

public record StockAlertResponse(Long id, String name, String sku, String deviceModel, int stock, int minStock,
		StockStatus stockStatus) {
}
