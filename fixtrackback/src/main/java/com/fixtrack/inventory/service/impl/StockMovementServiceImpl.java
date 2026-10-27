package com.fixtrack.inventory.service.impl;

import java.time.LocalDate;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fixtrack.common.BusinessRuleException;
import com.fixtrack.common.PageResponse;
import com.fixtrack.inventory.dto.StockMovementResponse;
import com.fixtrack.inventory.entity.StockMovementType;
import com.fixtrack.inventory.mapper.SparePartMapper;
import com.fixtrack.inventory.repository.StockMovementRepository;
import com.fixtrack.inventory.service.StockMovementService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StockMovementServiceImpl implements StockMovementService {

	private final StockMovementRepository stockMovementRepository;
	private final SparePartMapper sparePartMapper;

	@Override
	@Transactional(readOnly = true)
	public PageResponse<StockMovementResponse> search(Long sparePartId, StockMovementType type, LocalDate from,
			LocalDate to, Pageable pageable) {
		if (from != null && to != null && from.isAfter(to)) {
			throw new BusinessRuleException("La fecha inicial no puede ser posterior a la final");
		}
		return PageResponse.from(stockMovementRepository
				.search(sparePartId, type, from == null ? null : from.atStartOfDay(),
						to == null ? null : to.plusDays(1).atStartOfDay(), pageable)
				.map(sparePartMapper::toMovement));
	}
}
