package com.fixtrack.intervention.service;

import java.math.BigDecimal;
import java.util.List;

import com.fixtrack.infra.security.AuthenticatedUser;
import com.fixtrack.intervention.dto.InterventionPartsResponse;
import com.fixtrack.intervention.dto.SparePartUsageRequest;
import com.fixtrack.intervention.dto.UpdateSparePartUsageRequest;
import com.fixtrack.intervention.entity.Intervention;
import com.fixtrack.intervention.entity.InterventionSparePart;
import com.fixtrack.inventory.dto.SparePartCandidateResponse;
import com.fixtrack.person.entity.Person;

public interface InterventionSparePartService {

	InterventionPartsResponse findByIntervention(Long interventionId, AuthenticatedUser actor);

	List<SparePartCandidateResponse> findCandidates(Long interventionId, Long partTypeId, Long deviceModelId,
			AuthenticatedUser actor);

	InterventionPartsResponse register(Long interventionId, SparePartUsageRequest request, AuthenticatedUser actor);

	InterventionPartsResponse correctQuantity(Long interventionId, Long usageId, UpdateSparePartUsageRequest request,
			AuthenticatedUser actor);

	InterventionPartsResponse remove(Long interventionId, Long usageId, AuthenticatedUser actor);

	List<InterventionSparePart> findUsages(Long interventionId);

	// Devuelve al inventario todos los repuestos de la orden (RETURN) y elimina sus consumos; devuelve cuántas unidades
	int returnAllToStock(Intervention intervention, Person actor);

	// Consume en la orden el repuesto que llegó por una solicitud, con su costo real; false si la orden ya no lo usa
	boolean registerReceivedPart(Intervention intervention, Long sparePartId, int quantity, BigDecimal unitCost,
			Person actor);
}
