package com.fixtrack.inventory.controller;

import java.net.URI;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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

import com.fixtrack.common.PageResponse;
import com.fixtrack.inventory.dto.SupplierRequest;
import com.fixtrack.inventory.dto.SupplierResponse;
import com.fixtrack.inventory.service.SupplierService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
public class SupplierController {

	private final SupplierService supplierService;

	@GetMapping
	@PreAuthorize("hasRole('COORDINATOR')")
	public PageResponse<SupplierResponse> list(@RequestParam(required = false) String search,
			@RequestParam(defaultValue = "true") boolean active,
			@PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
		return supplierService.search(search, active, pageable);
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasRole('COORDINATOR')")
	public SupplierResponse get(@PathVariable Long id) {
		return supplierService.findById(id);
	}

	@PostMapping
	@PreAuthorize("hasRole('COORDINATOR')")
	public ResponseEntity<SupplierResponse> create(@Valid @RequestBody SupplierRequest request) {
		SupplierResponse created = supplierService.create(request);
		return ResponseEntity.created(URI.create("/api/suppliers/" + created.id())).body(created);
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasRole('COORDINATOR')")
	public SupplierResponse update(@PathVariable Long id, @Valid @RequestBody SupplierRequest request) {
		return supplierService.update(id, request);
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('COORDINATOR')")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deactivate(@PathVariable Long id) {
		supplierService.deactivate(id);
	}
}
