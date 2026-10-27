package com.fixtrack.inventory.mapper;

import org.springframework.stereotype.Component;

import com.fixtrack.intervention.mapper.InterventionCode;
import com.fixtrack.inventory.dto.PartRequestResponse;
import com.fixtrack.inventory.dto.SuggestedSupplierResponse;
import com.fixtrack.inventory.entity.PartRequest;
import com.fixtrack.inventory.entity.SupplierSparePart;
import com.fixtrack.person.entity.Person;

@Component
public class PartRequestMapper {

	public PartRequestResponse toResponse(PartRequest request, SupplierSparePart preferred) {
		var part = request.getSparePart();
		var supplier = request.getSupplier();
		return new PartRequestResponse(
				request.getId(),
				request.getIntervention().getId(),
				InterventionCode.of(request.getIntervention()),
				request.getStatus(),
				part == null ? null : part.getId(),
				part == null ? null : part.getName(),
				part == null ? null : part.getSku(),
				request.getDescription(),
				request.getQuantity(),
				request.getEstimatedCost(),
				request.getActualCost(),
				supplier == null ? null : supplier.getId(),
				supplier == null ? null : supplier.getName(),
				preferred == null ? null : new SuggestedSupplierResponse(preferred.getSupplier().getId(),
						preferred.getSupplier().getName(), preferred.getLastCost(), preferred.getLeadTimeDays()),
				name(request.getRequestedBy()),
				name(request.getHandledBy()),
				request.getRejectionReason(),
				request.getRequestedAt(),
				request.getReceivedAt());
	}

	private String name(Person person) {
		return person == null ? null : person.getFullName();
	}
}
