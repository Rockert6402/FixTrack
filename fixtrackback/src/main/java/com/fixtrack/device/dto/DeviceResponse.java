package com.fixtrack.device.dto;

import com.fixtrack.device.entity.DeviceType;

public record DeviceResponse(
		Long id,
		Long deviceModelId,
		String modelName,
		String brandName,
		DeviceType deviceType,
		String imei,
		String serialNumber,
		String color,
		String observations) {
}
