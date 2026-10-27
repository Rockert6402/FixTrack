package com.fixtrack.inventory.dto;

import com.fixtrack.device.entity.DeviceType;

public record CompatibleModelResponse(Long id, String brandName, String name, DeviceType deviceType) {
}
