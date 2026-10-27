package com.fixtrack.person.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.fixtrack.person.entity.PersonSpecialty;
import com.fixtrack.person.entity.PersonSpecialtyId;

public interface PersonSpecialtyRepository extends JpaRepository<PersonSpecialty, PersonSpecialtyId> {

	// Especialidades de varias personas en una sola consulta, para armar un listado sin N+1
	@EntityGraph(attributePaths = "specialty")
	List<PersonSpecialty> findByPersonIdIn(Collection<Long> personIds);

	boolean existsBySpecialtyId(Long specialtyId);
}
