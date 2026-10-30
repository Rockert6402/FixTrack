package com.fixtrack.person.service;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.fixtrack.common.PageResponse;
import com.fixtrack.infra.security.AuthenticatedUser;
import com.fixtrack.person.dto.CustomerContactRequest;
import com.fixtrack.person.dto.CustomerRequest;
import com.fixtrack.person.dto.PersonRequest;
import com.fixtrack.person.dto.PersonResponse;
import com.fixtrack.person.dto.TechnicianSummary;
import com.fixtrack.person.entity.Availability;
import com.fixtrack.person.entity.DocumentType;
import com.fixtrack.person.entity.Person;
import com.fixtrack.person.entity.Role;

public interface PersonService {

	PageResponse<PersonResponse> search(String search, Role role, boolean staff, boolean active, Pageable pageable);

	PersonResponse findById(Long id);

	PersonResponse findByDocument(DocumentType documentType, String documentNumber);

	PersonResponse create(PersonRequest request);

	PersonResponse update(Long id, PersonRequest request);

	void deactivate(Long id);

	PersonResponse changeAvailability(Long id, Availability availability, AuthenticatedUser actor);

	PersonResponse updateCustomerContact(Long id, CustomerContactRequest request);

	// Para el ingreso de órdenes: reutiliza a la persona por documento o crea el cliente
	Person registerOrUpdateCustomer(CustomerRequest request);

	Person getActiveTechnician(Long id);

	Person getEntity(Long id);

	List<TechnicianSummary> findActiveTechnicians();
}
