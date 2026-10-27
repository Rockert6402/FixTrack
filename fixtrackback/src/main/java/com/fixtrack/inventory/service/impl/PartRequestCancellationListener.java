package com.fixtrack.inventory.service.impl;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.fixtrack.intervention.event.InterventionCancelledEvent;
import com.fixtrack.inventory.service.PartRequestService;

import lombok.RequiredArgsConstructor;

// Al cancelar una orden, en su misma transacción, se cancelan sus solicitudes que aún no se pidieron al proveedor
@Component
@RequiredArgsConstructor
public class PartRequestCancellationListener {

	private final PartRequestService partRequestService;

	@EventListener
	public void onInterventionCancelled(InterventionCancelledEvent event) {
		partRequestService.cancelNotOrdered(event.interventionId());
	}
}
