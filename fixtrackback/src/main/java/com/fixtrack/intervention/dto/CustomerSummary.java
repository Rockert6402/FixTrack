package com.fixtrack.intervention.dto;

import com.fixtrack.person.entity.DocumentType;

public record CustomerSummary(Long id, DocumentType documentType, String documentNumber, String fullName, String phone,
		String email) {
}
