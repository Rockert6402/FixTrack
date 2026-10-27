package com.fixtrack.inventory.dto.report;

import java.util.List;

public record InventoryReport(InventoryTotals totals, List<InventoryReportItem> items) {
}
