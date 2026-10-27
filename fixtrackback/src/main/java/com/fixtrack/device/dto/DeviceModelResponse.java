package com.fixtrack.device.dto;

import com.fixtrack.device.entity.DeviceType;

public record DeviceModelResponse(Long id, String name, DeviceType deviceType, Long brandId, String brandName) {
}
