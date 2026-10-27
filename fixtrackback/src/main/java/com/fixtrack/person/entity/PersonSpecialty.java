package com.fixtrack.person.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

// Especialidades de cada técnico
@Entity
@Table(name = "person_specialty")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PersonSpecialty {

	@EmbeddedId
	private PersonSpecialtyId id;

	@MapsId("personId")
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "person_id")
	private Person person;

	@MapsId("specialtyId")
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "specialty_id")
	private Specialty specialty;

	public PersonSpecialty(Person person, Specialty specialty) {
		this.id = new PersonSpecialtyId(person.getId(), specialty.getId());
		this.person = person;
		this.specialty = specialty;
	}
}
