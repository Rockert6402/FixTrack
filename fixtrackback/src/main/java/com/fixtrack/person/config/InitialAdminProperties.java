package com.fixtrack.person.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.fixtrack.person.entity.DocumentType;

@ConfigurationProperties("fixtrack.admin")
public record InitialAdminProperties(String email, String password, String fullName, DocumentType documentType,
		String documentNumber) {
}
