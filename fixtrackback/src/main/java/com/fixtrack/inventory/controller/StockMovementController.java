package com.fixtrack.inventory.controller;

import java.time.LocalDate;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fixtrack.common.PageResponse;
import com.fixtrack.inventory.dto.StockMovementResponse;
import com.fixtrack.inventory.entity.StockMovementType;
import com.fixtrack.inventory.service.StockMovementService;

import lombok.RequiredArgsConstructor;

// Solo lectura: el kárdex no tiene creación, edición ni borrado por la API
@RestController
@RequestMapping("/api/stock-movements")
@RequiredArgsConstructor
public class StockMovementController {

	private final StockMovementService stockMovementService;

	@GetMapping
	@PreAuthorize("hasRole('COORDINATOR')")
	public PageResponse<StockMovementResponse> list(@RequestParam(required = false) Long sparePartId,
			@RequestParam(required = false) StockMovementType type,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
			@PageableDefault(size = 20, sort = { "createdAt", "id" }, direction = Sort.Direction.DESC) Pageable pageable) {
		return stockMovementService.search(sparePartId, type, from, to, pageable);
	}
}
