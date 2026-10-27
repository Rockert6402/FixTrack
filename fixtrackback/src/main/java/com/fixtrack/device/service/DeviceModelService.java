package com.fixtrack.device.service;

import java.util.List;

import com.fixtrack.device.dto.DeviceModelRequest;
import com.fixtrack.device.dto.DeviceModelResponse;
import com.fixtrack.device.entity.DeviceModel;
import com.fixtrack.device.entity.DeviceType;

public interface DeviceModelService {

	List<DeviceModelResponse> search(Long brandId, DeviceType deviceType);

	DeviceModelResponse findById(Long id);

	DeviceModelResponse create(DeviceModelRequest request);

	DeviceModelResponse update(Long id, DeviceModelRequest request);

	void delete(Long id);

	// Para que otros dominios (repuestos, equipos) enlacen el modelo sin usar el repositorio de device
	DeviceModel getEntity(Long id);
}
