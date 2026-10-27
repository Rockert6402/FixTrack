package com.fixtrack.inventory.mapper;

import org.springframework.stereotype.Component;

import com.fixtrack.inventory.dto.PartTypeResponse;
import com.fixtrack.inventory.entity.PartType;

@Component
public class PartTypeMapper {

	public PartTypeResponse toResponse(PartType partType) {
		return new PartTypeResponse(partType.getId(), partType.getName());
	}
}
