package com.fixtrack.person.controller;

import java.net.URI;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fixtrack.common.PageResponse;
import com.fixtrack.infra.security.CurrentUserProvider;
import com.fixtrack.person.dto.AvailabilityRequest;
import com.fixtrack.person.dto.CustomerContactRequest;
import com.fixtrack.person.dto.PersonRequest;
import com.fixtrack.person.dto.PersonResponse;
import com.fixtrack.person.entity.DocumentType;
import com.fixtrack.person.entity.Role;
import com.fixtrack.person.service.PersonService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/people")
@RequiredArgsConstructor
public class PersonController {

	private final PersonService personService;
	private final CurrentUserProvider currentUserProvider;

	@GetMapping
	@PreAuthorize("hasRole('ADVISOR')")
	public PageResponse<PersonResponse> list(@RequestParam(required = false) String search,
			@RequestParam(required = false) Role role,
			@RequestParam(defaultValue = "false") boolean staff,
			@RequestParam(defaultValue = "true") boolean active,
			@PageableDefault(size = 20, sort = "fullName", direction = Sort.Direction.ASC) Pageable pageable) {
		return personService.search(search, role, staff, active, pageable);
	}

	@GetMapping("/lookup")
	@PreAuthorize("hasRole('ADVISOR')")
	public PersonResponse lookup(@RequestParam DocumentType documentType, @RequestParam String documentNumber) {
		return personService.findByDocument(documentType, documentNumber);
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN') or #id == principal.id()")
	public PersonResponse get(@PathVariable Long id) {
		return personService.findById(id);
	}

	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<PersonResponse> create(@Valid @RequestBody PersonRequest request) {
		PersonResponse created = personService.create(request);
		return ResponseEntity.created(URI.create("/api/people/" + created.id())).body(created);
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public PersonResponse update(@PathVariable Long id, @Valid @RequestBody PersonRequest request) {
		return personService.update(id, request);
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deactivate(@PathVariable Long id) {
		personService.deactivate(id);
	}

	@PatchMapping("/{id}/contact")
	@PreAuthorize("hasRole('ADVISOR')")
	public PersonResponse updateCustomerContact(@PathVariable Long id, @Valid @RequestBody CustomerContactRequest request) {
		return personService.updateCustomerContact(id, request);
	}

	@PatchMapping("/{id}/availability")
	@PreAuthorize("hasRole('TECHNICIAN')")
	public PersonResponse changeAvailability(@PathVariable Long id, @Valid @RequestBody AvailabilityRequest request) {
		return personService.changeAvailability(id, request.availability(), currentUserProvider.require());
	}
}
