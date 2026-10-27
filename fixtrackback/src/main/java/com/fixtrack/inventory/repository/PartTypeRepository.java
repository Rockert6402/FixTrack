package com.fixtrack.inventory.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fixtrack.inventory.entity.PartType;

public interface PartTypeRepository extends JpaRepository<PartType, Long> {

	Optional<PartType> findByName(String name);

	boolean existsByName(String name);

	boolean existsByNameAndIdNot(String name, Long id);
}
