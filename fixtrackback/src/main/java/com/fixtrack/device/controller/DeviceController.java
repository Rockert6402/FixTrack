package com.fixtrack.device.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fixtrack.device.dto.DeviceResponse;
import com.fixtrack.device.service.DeviceService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/devices")
@RequiredArgsConstructor
public class DeviceController {

	private final DeviceService deviceService;

	@GetMapping
	@PreAuthorize("hasRole('ADVISOR')")
	public List<DeviceResponse> listByOwner(@RequestParam Long ownerId) {
		return deviceService.findByOwner(ownerId);
	}
}
