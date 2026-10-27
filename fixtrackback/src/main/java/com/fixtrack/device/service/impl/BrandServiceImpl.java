package com.fixtrack.device.service.impl;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fixtrack.common.ConflictException;
import com.fixtrack.common.NotFoundException;
import com.fixtrack.device.dto.BrandRequest;
import com.fixtrack.device.dto.BrandResponse;
import com.fixtrack.device.entity.Brand;
import com.fixtrack.device.mapper.BrandMapper;
import com.fixtrack.device.repository.BrandRepository;
import com.fixtrack.device.repository.DeviceModelRepository;
import com.fixtrack.device.service.BrandService;

import lombok.RequiredArgsConstructor;

// Catálogo de marcas: nombres únicos y solo se eliminan si no tienen modelos
@Service
@RequiredArgsConstructor
public class BrandServiceImpl implements BrandService {

	private final BrandRepository brandRepository;
	private final DeviceModelRepository deviceModelRepository;
	private final BrandMapper brandMapper;

	@Override
	@Transactional(readOnly = true)
	public List<BrandResponse> findAll() {
		return brandRepository.findAll(Sort.by("name")).stream().map(brandMapper::toResponse).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public BrandResponse findById(Long id) {
		return brandMapper.toResponse(findBrand(id));
	}

	@Override
	@Transactional
	public BrandResponse create(BrandRequest request) {
		String name = request.name().trim();
		if (brandRepository.existsByName(name)) {
			throw duplicateName(name);
		}
		Brand brand = Brand.builder().name(name).build();
		return brandMapper.toResponse(brandRepository.save(brand));
	}

	@Override
	@Transactional
	public BrandResponse update(Long id, BrandRequest request) {
		Brand brand = findBrand(id);
		String name = request.name().trim();
		if (brandRepository.existsByNameAndIdNot(name, id)) {
			throw duplicateName(name);
		}
		brand.setName(name);
		return brandMapper.toResponse(brand);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		Brand brand = findBrand(id);
		if (deviceModelRepository.existsByBrandId(id)) {
			throw new ConflictException("No se puede eliminar la marca " + brand.getName() + " porque tiene modelos registrados");
		}
		brandRepository.delete(brand);
	}

	private Brand findBrand(Long id) {
		return brandRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("No existe la marca con id " + id));
	}

	private ConflictException duplicateName(String name) {
		return new ConflictException("Ya existe una marca con el nombre " + name);
	}
}
