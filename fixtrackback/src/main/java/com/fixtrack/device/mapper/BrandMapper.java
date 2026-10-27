package com.fixtrack.device.mapper;

import org.springframework.stereotype.Component;

import com.fixtrack.device.dto.BrandResponse;
import com.fixtrack.device.entity.Brand;

@Component
public class BrandMapper {

	public BrandResponse toResponse(Brand brand) {
		return new BrandResponse(brand.getId(), brand.getName());
	}
}
