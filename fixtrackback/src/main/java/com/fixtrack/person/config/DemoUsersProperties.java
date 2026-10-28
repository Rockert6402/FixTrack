package com.fixtrack.person.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.fixtrack.person.entity.DocumentType;
import com.fixtrack.person.entity.Role;

@ConfigurationProperties("fixtrack.demo-users")
public record DemoUsersProperties(String password, List<DemoUser> users) {

	public record DemoUser(String email, String fullName, Role role, DocumentType documentType, String documentNumber) {
	}
}
