package com.fixtrack.device.dto;

import com.fixtrack.device.entity.DeviceType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record DeviceModelRequest(
		@NotNull Long brandId,
		@NotBlank @Size(max = 100) String name,
		@NotNull DeviceType deviceType) {
}
