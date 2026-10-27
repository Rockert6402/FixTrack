package com.fixtrack.person.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.fixtrack.infra.security.AuthenticatedUser;
import com.fixtrack.person.dto.AuthUserResponse;
import com.fixtrack.person.dto.PersonResponse;
import com.fixtrack.person.dto.SpecialtyResponse;
import com.fixtrack.person.entity.Person;

@Component
public class PersonMapper {

	public PersonResponse toResponse(Person person, List<SpecialtyResponse> specialties) {
		return new PersonResponse(
				person.getId(),
				person.getDocumentType(),
				person.getDocumentNumber(),
				person.getFullName(),
				person.getPhone(),
				person.getEmail(),
				person.getRole(),
				person.getAvailability(),
				person.isActive(),
				specialties,
				person.getCreatedAt());
	}

	public AuthUserResponse toAuthUser(AuthenticatedUser user) {
		return new AuthUserResponse(user.id(), user.fullName(), user.email(), user.role());
	}
}
