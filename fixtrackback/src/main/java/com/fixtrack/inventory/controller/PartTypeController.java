package com.fixtrack.inventory.controller;

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

import com.fixtrack.inventory.dto.PartTypeRequest;
import com.fixtrack.inventory.dto.PartTypeResponse;
import com.fixtrack.inventory.service.PartTypeService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/part-types")
@RequiredArgsConstructor
public class PartTypeController {

	private final PartTypeService partTypeService;

	@GetMapping
	@PreAuthorize("isAuthenticated()")
	public List<PartTypeResponse> list() {
		return partTypeService.findAll();
	}

	@GetMapping("/{id}")
	@PreAuthorize("isAuthenticated()")
	public PartTypeResponse get(@PathVariable Long id) {
		return partTypeService.findById(id);
	}

	@PostMapping
	@PreAuthorize("hasRole('COORDINATOR')")
	public ResponseEntity<PartTypeResponse> create(@Valid @RequestBody PartTypeRequest request) {
		PartTypeResponse created = partTypeService.create(request);
		return ResponseEntity.created(URI.create("/api/part-types/" + created.id())).body(created);
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasRole('COORDINATOR')")
	public PartTypeResponse update(@PathVariable Long id, @Valid @RequestBody PartTypeRequest request) {
		return partTypeService.update(id, request);
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('COORDINATOR')")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		partTypeService.delete(id);
	}
}
