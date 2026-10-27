package com.fixtrack.device.mapper;

import org.springframework.stereotype.Component;

import com.fixtrack.device.dto.DeviceModelResponse;
import com.fixtrack.device.entity.DeviceModel;

@Component
public class DeviceModelMapper {

	public DeviceModelResponse toResponse(DeviceModel model) {
		return new DeviceModelResponse(model.getId(), model.getName(), model.getDeviceType(),
				model.getBrand().getId(), model.getBrand().getName());
	}
}
