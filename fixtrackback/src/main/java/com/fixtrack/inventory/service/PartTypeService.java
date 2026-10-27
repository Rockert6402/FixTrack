package com.fixtrack.inventory.service;

import java.util.List;

import com.fixtrack.inventory.dto.PartTypeRequest;
import com.fixtrack.inventory.dto.PartTypeResponse;

public interface PartTypeService {

	List<PartTypeResponse> findAll();

	PartTypeResponse findById(Long id);

	PartTypeResponse create(PartTypeRequest request);

	PartTypeResponse update(Long id, PartTypeRequest request);

	void delete(Long id);
}
