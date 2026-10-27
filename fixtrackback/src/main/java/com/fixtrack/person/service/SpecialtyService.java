package com.fixtrack.person.service;

import java.util.List;

import com.fixtrack.person.dto.SpecialtyRequest;
import com.fixtrack.person.dto.SpecialtyResponse;

public interface SpecialtyService {

	List<SpecialtyResponse> findAll();

	SpecialtyResponse findById(Long id);

	SpecialtyResponse create(SpecialtyRequest request);

	SpecialtyResponse update(Long id, SpecialtyRequest request);

	void delete(Long id);
}
