package com.fixtrack.inventory.service.impl;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fixtrack.common.ConflictException;
import com.fixtrack.common.NotFoundException;
import com.fixtrack.inventory.dto.PartTypeRequest;
import com.fixtrack.inventory.dto.PartTypeResponse;
import com.fixtrack.inventory.entity.PartType;
import com.fixtrack.inventory.mapper.PartTypeMapper;
import com.fixtrack.inventory.repository.PartTypeRepository;
import com.fixtrack.inventory.repository.SparePartRepository;
import com.fixtrack.inventory.service.PartTypeService;

import lombok.RequiredArgsConstructor;

// Catálogo de tipos de repuesto: nombres únicos y solo se eliminan si ningún repuesto los usa
@Service
@RequiredArgsConstructor
public class PartTypeServiceImpl implements PartTypeService {

	private final PartTypeRepository partTypeRepository;
	private final SparePartRepository sparePartRepository;
	private final PartTypeMapper partTypeMapper;

	@Override
	@Transactional(readOnly = true)
	public List<PartTypeResponse> findAll() {
		return partTypeRepository.findAll(Sort.by("name")).stream().map(partTypeMapper::toResponse).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public PartTypeResponse findById(Long id) {
		return partTypeMapper.toResponse(findPartType(id));
	}

	@Override
	@Transactional
	public PartTypeResponse create(PartTypeRequest request) {
		String name = request.name().trim();
		if (partTypeRepository.existsByName(name)) {
			throw duplicateName(name);
		}
		PartType partType = PartType.builder().name(name).build();
		return partTypeMapper.toResponse(partTypeRepository.save(partType));
	}

	@Override
	@Transactional
	public PartTypeResponse update(Long id, PartTypeRequest request) {
		PartType partType = findPartType(id);
		String name = request.name().trim();
		if (partTypeRepository.existsByNameAndIdNot(name, id)) {
			throw duplicateName(name);
		}
		partType.setName(name);
		return partTypeMapper.toResponse(partType);
	}

	// Cuentan también los repuestos dados de baja, porque conservan su historial
	@Override
	@Transactional
	public void delete(Long id) {
		PartType partType = findPartType(id);
		if (sparePartRepository.existsByPartTypeId(id)) {
			throw new ConflictException(
					"No se puede eliminar el tipo " + partType.getName() + " porque tiene repuestos registrados");
		}
		partTypeRepository.delete(partType);
	}

	private PartType findPartType(Long id) {
		return partTypeRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("No existe el tipo de repuesto con id " + id));
	}

	private ConflictException duplicateName(String name) {
		return new ConflictException("Ya existe un tipo de repuesto con el nombre " + name);
	}
}
