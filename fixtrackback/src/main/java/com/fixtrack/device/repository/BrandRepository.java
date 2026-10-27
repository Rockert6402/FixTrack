package com.fixtrack.device.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fixtrack.device.entity.Brand;

public interface BrandRepository extends JpaRepository<Brand, Long> {

	Optional<Brand> findByName(String name);

	boolean existsByName(String name);

	boolean existsByNameAndIdNot(String name, Long id);
}
