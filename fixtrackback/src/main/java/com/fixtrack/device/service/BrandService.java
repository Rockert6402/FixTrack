package com.fixtrack.device.service;

import java.util.List;

import com.fixtrack.device.dto.BrandRequest;
import com.fixtrack.device.dto.BrandResponse;

public interface BrandService {

	List<BrandResponse> findAll();

	BrandResponse findById(Long id);

	BrandResponse create(BrandRequest request);

	BrandResponse update(Long id, BrandRequest request);

	void delete(Long id);
}
