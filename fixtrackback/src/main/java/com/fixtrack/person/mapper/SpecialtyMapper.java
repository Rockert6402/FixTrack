package com.fixtrack.person.mapper;

import org.springframework.stereotype.Component;

import com.fixtrack.person.dto.SpecialtyResponse;
import com.fixtrack.person.entity.Specialty;

@Component
public class SpecialtyMapper {

	public SpecialtyResponse toResponse(Specialty specialty) {
		return new SpecialtyResponse(specialty.getId(), specialty.getName());
	}
}
