package com.fixtrack.intervention.entity;

public enum InterventionStatus {
	RECEIVED, IN_DIAGNOSIS, WAITING_PARTS, IN_REPAIR, READY, DELIVERED, CANCELLED;

	// Transiciones del flujo de reparación; la cancelación se maneja aparte
	public boolean canChangeTo(InterventionStatus next) {
		return switch (this) {
			case RECEIVED -> next == IN_DIAGNOSIS;
			case IN_DIAGNOSIS -> next == WAITING_PARTS || next == IN_REPAIR;
			case WAITING_PARTS -> next == IN_REPAIR;
			case IN_REPAIR -> next == READY || next == WAITING_PARTS;
			case READY -> next == DELIVERED;
			case DELIVERED, CANCELLED -> false;
		};
	}

	// Sin reparación (no reparable o rechazado) la orden solo puede pasar a lista para entregar
	public boolean canChangeTo(InterventionStatus next, RepairOutcome outcome) {
		if (outcome != null && outcome.endsWithoutRepair()) {
			return next == READY && isInProgress();
		}
		return canChangeTo(next);
	}

	// Estados en que el técnico trabaja la orden: diagnóstico, espera de repuestos o reparación
	public boolean isInProgress() {
		return this == IN_DIAGNOSIS || this == WAITING_PARTS || this == IN_REPAIR;
	}

	public boolean isClosed() {
		return this == DELIVERED || this == CANCELLED;
	}

	// Una orden puede estar atrasada mientras no esté lista, entregada ni cancelada
	public boolean canBeOverdue() {
		return this != READY && !isClosed();
	}
}
