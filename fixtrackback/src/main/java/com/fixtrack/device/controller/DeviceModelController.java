package com.fixtrack.device.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fixtrack.device.dto.DeviceModelRequest;
import com.fixtrack.device.dto.DeviceModelResponse;
import com.fixtrack.device.entity.DeviceType;
import com.fixtrack.device.service.DeviceModelService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/device-models")
@RequiredArgsConstructor
public class DeviceModelController {

	private final DeviceModelService deviceModelService;

	@GetMapping
	@PreAuthorize("isAuthenticated()")
	public List<DeviceModelResponse> list(@RequestParam(required = false) Long brandId,
			@RequestParam(required = false) DeviceType deviceType) {
		return deviceModelService.search(brandId, deviceType);
	}

	@GetMapping("/{id}")
	@PreAuthorize("isAuthenticated()")
	public DeviceModelResponse get(@PathVariable Long id) {
		return deviceModelService.findById(id);
	}

	@PostMapping
	@PreAuthorize("hasRole('COORDINATOR')")
	public ResponseEntity<DeviceModelResponse> create(@Valid @RequestBody DeviceModelRequest request) {
		DeviceModelResponse created = deviceModelService.create(request);
		return ResponseEntity.created(URI.create("/api/device-models/" + created.id())).body(created);
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasRole('COORDINATOR')")
	public DeviceModelResponse update(@PathVariable Long id, @Valid @RequestBody DeviceModelRequest request) {
		return deviceModelService.update(id, request);
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('COORDINATOR')")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		deviceModelService.delete(id);
	}
}
