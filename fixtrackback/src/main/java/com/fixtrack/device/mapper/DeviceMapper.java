package com.fixtrack.device.mapper;

import org.springframework.stereotype.Component;

import com.fixtrack.device.dto.DeviceResponse;
import com.fixtrack.device.entity.Device;

@Component
public class DeviceMapper {

	public DeviceResponse toResponse(Device device) {
		return new DeviceResponse(device.getId(), device.getDeviceModel().getId(), device.getDeviceModel().getName(),
				device.getDeviceModel().getBrand().getName(), device.getDeviceModel().getDeviceType(), device.getImei(),
				device.getSerialNumber(), device.getColor(), device.getObservations());
	}
}
