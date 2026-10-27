package com.fixtrack.inventory.service;

import java.time.LocalDate;

import org.springframework.data.domain.Pageable;

import com.fixtrack.common.PageResponse;
import com.fixtrack.inventory.dto.StockMovementResponse;
import com.fixtrack.inventory.entity.StockMovementType;

// Consulta del kárdex; los movimientos solo los crea StockService y nunca se editan
public interface StockMovementService {

	PageResponse<StockMovementResponse> search(Long sparePartId, StockMovementType type, LocalDate from, LocalDate to,
			Pageable pageable);
}
