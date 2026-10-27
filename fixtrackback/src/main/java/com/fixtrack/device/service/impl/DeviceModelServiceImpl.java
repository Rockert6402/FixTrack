package com.fixtrack.device.service.impl;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fixtrack.common.ConflictException;
import com.fixtrack.common.NotFoundException;
import com.fixtrack.device.dto.DeviceModelRequest;
import com.fixtrack.device.dto.DeviceModelResponse;
import com.fixtrack.device.entity.Brand;
import com.fixtrack.device.entity.DeviceModel;
import com.fixtrack.device.entity.DeviceType;
import com.fixtrack.device.mapper.DeviceModelMapper;
import com.fixtrack.device.repository.BrandRepository;
import com.fixtrack.device.repository.DeviceModelRepository;
import com.fixtrack.device.service.DeviceModelService;

import lombok.RequiredArgsConstructor;

// Catálogo de modelos: el nombre es único dentro de cada marca y solo se eliminan si nada los usa
@Service
@RequiredArgsConstructor
public class DeviceModelServiceImpl implements DeviceModelService {

	private final DeviceModelRepository deviceModelRepository;
	private final BrandRepository brandRepository;
	private final DeviceModelMapper deviceModelMapper;

	@Override
	@Transactional(readOnly = true)
	public List<DeviceModelResponse> search(Long brandId, DeviceType deviceType) {
		return deviceModelRepository.search(brandId, deviceType).stream().map(deviceModelMapper::toResponse).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public DeviceModelResponse findById(Long id) {
		return deviceModelMapper.toResponse(getEntity(id));
	}

	@Override
	@Transactional
	public DeviceModelResponse create(DeviceModelRequest request) {
		Brand brand = findBrand(request.brandId());
		String name = request.name().trim();
		if (deviceModelRepository.existsByBrandIdAndName(brand.getId(), name)) {
			throw duplicateName(brand, name);
		}
		DeviceModel model = DeviceModel.builder().brand(brand).name(name).deviceType(request.deviceType()).build();
		return deviceModelMapper.toResponse(deviceModelRepository.save(model));
	}

	@Override
	@Transactional
	public DeviceModelResponse update(Long id, DeviceModelRequest request) {
		DeviceModel model = getEntity(id);
		Brand brand = findBrand(request.brandId());
		String name = request.name().trim();
		if (deviceModelRepository.existsByBrandIdAndNameAndIdNot(brand.getId(), name, id)) {
			throw duplicateName(brand, name);
		}
		model.setBrand(brand);
		model.setName(name);
		model.setDeviceType(request.deviceType());
		return deviceModelMapper.toResponse(model);
	}

	// Equipos y repuestos de otros dominios lo referencian: la base de datos decide si se puede borrar
	@Override
	@Transactional
	public void delete(Long id) {
		DeviceModel model = getEntity(id);
		try {
			deviceModelRepository.delete(model);
			deviceModelRepository.flush();
		} catch (DataIntegrityViolationException e) {
			throw new ConflictException(
					"No se puede eliminar el modelo " + model.getName() + " porque tiene equipos o repuestos asociados");
		}
	}

	@Override
	@Transactional(readOnly = true)
	public DeviceModel getEntity(Long id) {
		return deviceModelRepository.findWithBrandById(id)
				.orElseThrow(() -> new NotFoundException("No existe el modelo con id " + id));
	}

	private Brand findBrand(Long brandId) {
		return brandRepository.findById(brandId)
				.orElseThrow(() -> new NotFoundException("No existe la marca con id " + brandId));
	}

	private ConflictException duplicateName(Brand brand, String name) {
		return new ConflictException("La marca " + brand.getName() + " ya tiene un modelo llamado " + name);
	}
}
