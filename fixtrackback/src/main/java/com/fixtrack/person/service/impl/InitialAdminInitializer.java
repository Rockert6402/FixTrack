package com.fixtrack.person.service.impl;

import java.util.Locale;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fixtrack.person.config.InitialAdminProperties;
import com.fixtrack.person.entity.Person;
import com.fixtrack.person.entity.Role;
import com.fixtrack.person.repository.PersonRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// Al arrancar crea el administrador inicial si no hay ninguno activo; la contraseña sale de ADMIN_PASSWORD
@Slf4j
@Component
@RequiredArgsConstructor
public class InitialAdminInitializer implements ApplicationRunner {

	private final PersonRepository personRepository;
	private final PasswordEncoder passwordEncoder;
	private final InitialAdminProperties properties;

	@Override
	@Transactional
	public void run(ApplicationArguments args) {
		if (personRepository.existsByRoleAndActiveTrue(Role.ADMIN)) {
			return;
		}
		if (properties.password() == null || properties.password().isBlank()) {
			log.warn("No hay un administrador activo y falta ADMIN_PASSWORD: no se creó el administrador inicial");
			return;
		}
		String email = properties.email().trim().toLowerCase(Locale.ROOT);
		if (personRepository.existsByEmail(email)) {
			log.warn("No se creó el administrador inicial: el correo {} ya pertenece a otra persona", email);
			return;
		}

		personRepository.save(Person.builder()
				.documentType(properties.documentType())
				.documentNumber(properties.documentNumber())
				.fullName(properties.fullName())
				.email(email)
				.password(passwordEncoder.encode(properties.password()))
				.role(Role.ADMIN)
				.build());
		log.info("Administrador inicial creado con el correo {}", email);
	}
}
