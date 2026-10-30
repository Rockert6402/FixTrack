package com.fixtrack.person.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fixtrack.common.BusinessRuleException;
import com.fixtrack.common.ConflictException;
import com.fixtrack.person.dto.CustomerContactRequest;
import com.fixtrack.person.dto.PersonResponse;
import com.fixtrack.person.entity.DocumentType;
import com.fixtrack.person.entity.Person;
import com.fixtrack.person.entity.Role;
import com.fixtrack.person.mapper.PersonMapper;
import com.fixtrack.person.repository.PersonRepository;
import com.fixtrack.person.repository.PersonSpecialtyRepository;

@ExtendWith(MockitoExtension.class)
class PersonServiceImplTest {

	@Mock
	private PersonRepository personRepository;

	@Mock
	private PersonSpecialtyRepository personSpecialtyRepository;

	@Spy
	private PersonMapper personMapper;

	@InjectMocks
	private PersonServiceImpl personService;

	private Person person(Role role) {
		return Person.builder().id(7L).documentType(DocumentType.CC).documentNumber("43567890").fullName("Ana Ríos")
				.phone("3001234567").email("ana@correo.co").role(role).build();
	}

	@Test
	void shouldUpdateCustomerPhoneAndClearEmailMarkedAsNone() {
		Person customer = person(Role.CUSTOMER);
		given(personRepository.findById(7L)).willReturn(Optional.of(customer));

		PersonResponse updated = personService.updateCustomerContact(7L,
				new CustomerContactRequest(" 3109876543 ", false, null, true));

		assertThat(updated.phone()).isEqualTo("3109876543");
		assertThat(updated.email()).isNull();
	}

	@Test
	void shouldRejectContactEditOfInternalUser() {
		given(personRepository.findById(7L)).willReturn(Optional.of(person(Role.TECHNICIAN)));

		assertThatThrownBy(() -> personService.updateCustomerContact(7L,
				new CustomerContactRequest("3109876543", false, "tec@fixtrack.local", false)))
				.isInstanceOf(BusinessRuleException.class);
	}

	@Test
	void shouldRequirePhoneOrNoPhoneMark() {
		given(personRepository.findById(7L)).willReturn(Optional.of(person(Role.CUSTOMER)));

		assertThatThrownBy(() -> personService.updateCustomerContact(7L,
				new CustomerContactRequest(null, false, "ana@correo.co", false)))
				.isInstanceOf(BusinessRuleException.class)
				.hasMessageContaining("celular");
	}

	@Test
	void shouldRejectEmailOfAnotherPerson() {
		given(personRepository.findById(7L)).willReturn(Optional.of(person(Role.CUSTOMER)));
		given(personRepository.existsByEmailAndIdNot("otro@correo.co", 7L)).willReturn(true);

		assertThatThrownBy(() -> personService.updateCustomerContact(7L,
				new CustomerContactRequest("3001234567", false, "Otro@Correo.co", false)))
				.isInstanceOf(ConflictException.class);
	}
}
