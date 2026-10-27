package com.fixtrack.person.controller;

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

import com.fixtrack.person.dto.SpecialtyRequest;
import com.fixtrack.person.dto.SpecialtyResponse;
import com.fixtrack.person.service.SpecialtyService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/specialties")
@RequiredArgsConstructor
public class SpecialtyController {

	private final SpecialtyService specialtyService;

	@GetMapping
	@PreAuthorize("isAuthenticated()")
	public List<SpecialtyResponse> list() {
		return specialtyService.findAll();
	}

	@GetMapping("/{id}")
	@PreAuthorize("isAuthenticated()")
	public SpecialtyResponse get(@PathVariable Long id) {
		return specialtyService.findById(id);
	}

	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<SpecialtyResponse> create(@Valid @RequestBody SpecialtyRequest request) {
		SpecialtyResponse created = specialtyService.create(request);
		return ResponseEntity.created(URI.create("/api/specialties/" + created.id())).body(created);
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public SpecialtyResponse update(@PathVariable Long id, @Valid @RequestBody SpecialtyRequest request) {
		return specialtyService.update(id, request);
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		specialtyService.delete(id);
	}
}
