package com.fixtrack.person.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fixtrack.person.entity.Availability;
import com.fixtrack.person.entity.DocumentType;
import com.fixtrack.person.entity.Role;

public record PersonResponse(
		Long id,
		DocumentType documentType,
		String documentNumber,
		String fullName,
		String phone,
		String email,
		Role role,
		Availability availability,
		boolean active,
		List<SpecialtyResponse> specialties,
		LocalDateTime createdAt) {
}
