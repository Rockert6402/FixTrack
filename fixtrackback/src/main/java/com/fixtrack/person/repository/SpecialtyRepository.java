package com.fixtrack.person.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fixtrack.person.entity.Specialty;

public interface SpecialtyRepository extends JpaRepository<Specialty, Long> {

	Optional<Specialty> findByName(String name);

	boolean existsByName(String name);

	boolean existsByNameAndIdNot(String name, Long id);
}
