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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fixtrack.device.dto.BrandRequest;
import com.fixtrack.device.dto.BrandResponse;
import com.fixtrack.device.service.BrandService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/brands")
@RequiredArgsConstructor
public class BrandController {

	private final BrandService brandService;

	@GetMapping
	@PreAuthorize("isAuthenticated()")
	public List<BrandResponse> list() {
		return brandService.findAll();
	}

	@GetMapping("/{id}")
	@PreAuthorize("isAuthenticated()")
	public BrandResponse get(@PathVariable Long id) {
		return brandService.findById(id);
	}

	@PostMapping
	@PreAuthorize("hasRole('COORDINATOR')")
	public ResponseEntity<BrandResponse> create(@Valid @RequestBody BrandRequest request) {
		BrandResponse created = brandService.create(request);
		return ResponseEntity.created(URI.create("/api/brands/" + created.id())).body(created);
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasRole('COORDINATOR')")
	public BrandResponse update(@PathVariable Long id, @Valid @RequestBody BrandRequest request) {
		return brandService.update(id, request);
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('COORDINATOR')")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		brandService.delete(id);
	}
}
