package com.fixtrack.device.service;

import java.util.List;

import com.fixtrack.device.dto.DeviceRequest;
import com.fixtrack.device.dto.DeviceResponse;
import com.fixtrack.device.entity.Device;
import com.fixtrack.person.entity.Person;

public interface DeviceService {

	List<DeviceResponse> findByOwner(Long ownerId);

	// Para el ingreso de órdenes: reutiliza el equipo del cliente o lo registra
	Device registerOrReuse(Person owner, DeviceRequest request);
}
