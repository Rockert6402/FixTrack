package com.fixtrack.inventory.service;

import org.springframework.data.domain.Pageable;

import com.fixtrack.common.PageResponse;
import com.fixtrack.inventory.dto.SupplierRequest;
import com.fixtrack.inventory.dto.SupplierResponse;

public interface SupplierService {

	PageResponse<SupplierResponse> search(String search, boolean active, Pageable pageable);

	SupplierResponse findById(Long id);

	SupplierResponse create(SupplierRequest request);

	SupplierResponse update(Long id, SupplierRequest request);

	void deactivate(Long id);
}
