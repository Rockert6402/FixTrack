package com.fixtrack.intervention.controller;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fixtrack.infra.security.CurrentUserProvider;
import com.fixtrack.intervention.dto.dashboard.DashboardResponse;
import com.fixtrack.intervention.dto.report.MaintenanceReport;
import com.fixtrack.intervention.dto.report.ProfitReport;
import com.fixtrack.intervention.dto.report.ReportFile;
import com.fixtrack.intervention.service.DashboardService;
import com.fixtrack.intervention.service.ReportService;
import com.fixtrack.inventory.dto.SparePartFilter;
import com.fixtrack.inventory.dto.report.InventoryReport;
import com.fixtrack.inventory.entity.PartQuality;
import com.fixtrack.inventory.entity.StockStatus;
import com.fixtrack.inventory.service.SparePartService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ReportController {

	private final DashboardService dashboardService;
	private final ReportService reportService;
	private final SparePartService sparePartService;
	private final CurrentUserProvider currentUserProvider;

	// El contenido depende del rol de quien consulta
	@GetMapping("/api/dashboard")
	@PreAuthorize("isAuthenticated()")
	public DashboardResponse dashboard() {
		return dashboardService.forUser(currentUserProvider.require());
	}

	// Existencias, valor a costo y alertas; con los mismos filtros del inventario
	@GetMapping("/api/reports/inventory")
	@PreAuthorize("hasRole('COORDINATOR')")
	public InventoryReport inventory(@RequestParam(required = false) String search,
			@RequestParam(required = false) Long partTypeId, @RequestParam(required = false) Long deviceModelId,
			@RequestParam(required = false) Long brandId, @RequestParam(required = false) PartQuality quality,
			@RequestParam(required = false) StockStatus stockStatus,
			@RequestParam(defaultValue = "false") boolean alertsOnly) {
		return sparePartService.inventoryReport(
				new SparePartFilter(search, partTypeId, deviceModelId, brandId, quality, stockStatus, alertsOnly));
	}

	@GetMapping("/api/reports/maintenance")
	@PreAuthorize("hasRole('ADMIN')")
	public MaintenanceReport maintenance(
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
		return reportService.maintenance(from, to);
	}

	// El técnico recibe solo sus órdenes, aunque envíe otro technicianId
	@GetMapping("/api/reports/profit")
	@PreAuthorize("hasAnyRole('ADMIN', 'TECHNICIAN')")
	public ProfitReport profit(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
			@RequestParam(required = false) Long technicianId) {
		return reportService.profit(from, to, technicianId, currentUserProvider.require());
	}

	@GetMapping("/api/reports/profit/export")
	@PreAuthorize("hasAnyRole('ADMIN', 'TECHNICIAN')")
	public ResponseEntity<byte[]> exportProfit(
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
			@RequestParam(required = false) Long technicianId) {
		ReportFile file = reportService.exportProfit(from, to, technicianId, currentUserProvider.require());
		return ResponseEntity.ok()
				.contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
				.header(HttpHeaders.CONTENT_DISPOSITION,
						ContentDisposition.attachment().filename(file.fileName()).build().toString())
				.body(file.content());
	}
}
