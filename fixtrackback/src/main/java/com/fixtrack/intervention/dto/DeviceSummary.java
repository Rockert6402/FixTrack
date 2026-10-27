package com.fixtrack.intervention.dto;

import com.fixtrack.device.entity.DeviceType;

public record DeviceSummary(Long id, String brandName, String modelName, DeviceType deviceType, String imei,
		String serialNumber, String color, String observations) {
}
