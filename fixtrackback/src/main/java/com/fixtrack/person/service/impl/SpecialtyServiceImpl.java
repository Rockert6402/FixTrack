package com.fixtrack.person.service.impl;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fixtrack.common.ConflictException;
import com.fixtrack.common.NotFoundException;
import com.fixtrack.person.dto.SpecialtyRequest;
import com.fixtrack.person.dto.SpecialtyResponse;
import com.fixtrack.person.entity.Specialty;
import com.fixtrack.person.mapper.SpecialtyMapper;
import com.fixtrack.person.repository.PersonSpecialtyRepository;
import com.fixtrack.person.repository.SpecialtyRepository;
import com.fixtrack.person.service.SpecialtyService;

import lombok.RequiredArgsConstructor;

// Catálogo de especialidades: nombres únicos y solo se eliminan si ningún técnico las tiene
@Service
@RequiredArgsConstructor
public class SpecialtyServiceImpl implements SpecialtyService {

	private final SpecialtyRepository specialtyRepository;
	private final PersonSpecialtyRepository personSpecialtyRepository;
	private final SpecialtyMapper specialtyMapper;

	@Override
	@Transactional(readOnly = true)
	public List<SpecialtyResponse> findAll() {
		return specialtyRepository.findAll(Sort.by("name")).stream().map(specialtyMapper::toResponse).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public SpecialtyResponse findById(Long id) {
		return specialtyMapper.toResponse(findSpecialty(id));
	}

	@Override
	@Transactional
	public SpecialtyResponse create(SpecialtyRequest request) {
		String name = request.name().trim();
		if (specialtyRepository.existsByName(name)) {
			throw duplicateName(name);
		}
		return specialtyMapper.toResponse(specialtyRepository.save(Specialty.builder().name(name).build()));
	}

	@Override
	@Transactional
	public SpecialtyResponse update(Long id, SpecialtyRequest request) {
		Specialty specialty = findSpecialty(id);
		String name = request.name().trim();
		if (specialtyRepository.existsByNameAndIdNot(name, id)) {
			throw duplicateName(name);
		}
		specialty.setName(name);
		return specialtyMapper.toResponse(specialty);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		Specialty specialty = findSpecialty(id);
		if (personSpecialtyRepository.existsBySpecialtyId(id)) {
			throw new ConflictException(
					"No se puede eliminar la especialidad " + specialty.getName() + " porque hay técnicos que la tienen");
		}
		specialtyRepository.delete(specialty);
	}

	private Specialty findSpecialty(Long id) {
		return specialtyRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("No existe la especialidad con id " + id));
	}

	private ConflictException duplicateName(String name) {
		return new ConflictException("Ya existe una especialidad con el nombre " + name);
	}
}
