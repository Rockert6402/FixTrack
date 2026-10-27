package com.fixtrack.inventory.dto;

import com.fixtrack.inventory.entity.PartQuality;
import com.fixtrack.inventory.entity.StockStatus;

public record SparePartFilter(String search, Long partTypeId, Long deviceModelId, Long brandId, PartQuality quality,
		StockStatus stockStatus,
		boolean alertsOnly) {
}
