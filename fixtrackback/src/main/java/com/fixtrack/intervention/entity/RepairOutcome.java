package com.fixtrack.intervention.entity;

public enum RepairOutcome {
	REPAIRED, NOT_REPAIRABLE, REJECTED_BY_CUSTOMER;

	// No reparable o rechazado: se cobra solo el diagnóstico, sin repuestos ni garantía, y no se reabre
	public boolean endsWithoutRepair() {
		return this != REPAIRED;
	}
}
