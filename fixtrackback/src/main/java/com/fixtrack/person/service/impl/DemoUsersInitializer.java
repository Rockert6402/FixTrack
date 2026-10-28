package com.fixtrack.person.service.impl;

import java.util.Locale;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fixtrack.person.config.DemoUsersProperties;
import com.fixtrack.person.entity.Availability;
import com.fixtrack.person.entity.Person;
import com.fixtrack.person.entity.Role;
import com.fixtrack.person.repository.PersonRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// Al arrancar crea los usuarios de prueba de cada rol que falten; solo si DEMO_USERS_PASSWORD tiene valor
@Slf4j
@Component
@RequiredArgsConstructor
public class DemoUsersInitializer implements ApplicationRunner {

	private final PersonRepository personRepository;
	private final PasswordEncoder passwordEncoder;
	private final DemoUsersProperties properties;

	@Override
	@Transactional
	public void run(ApplicationArguments args) {
		if (properties.password() == null || properties.password().isBlank() || properties.users() == null) {
			return;
		}
		var passwordHash = passwordEncoder.encode(properties.password());
		for (var user : properties.users()) {
			String email = user.email().trim().toLowerCase(Locale.ROOT);
			if (personRepository.existsByEmail(email)
					|| personRepository.existsByDocumentTypeAndDocumentNumber(user.documentType(), user.documentNumber())) {
				continue;
			}
			personRepository.save(Person.builder()
					.documentType(user.documentType())
					.documentNumber(user.documentNumber())
					.fullName(user.fullName())
					.email(email)
					.password(passwordHash)
					.role(user.role())
					.availability(user.role() == Role.TECHNICIAN ? Availability.AVAILABLE : null)
					.build());
			log.info("Usuario de prueba {} creado con el correo {}", user.role(), email);
		}
	}
}
