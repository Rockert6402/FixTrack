package com.fixtrack.intervention.service.impl;

import java.util.Map;

import com.fixtrack.cash.entity.PaymentMethod;
import com.fixtrack.cash.entity.PaymentType;
import com.fixtrack.device.entity.DeviceType;
import com.fixtrack.intervention.entity.RepairOutcome;
import com.fixtrack.intervention.entity.ServiceType;
import com.fixtrack.person.entity.DocumentType;

// Textos en español de los valores que se imprimen en los comprobantes
final class ReceiptLabels {

	static final Map<ServiceType, String> SERVICE_TYPES = Map.of(
			ServiceType.SCREEN_REPLACEMENT, "Cambio de pantalla",
			ServiceType.BATTERY_REPLACEMENT, "Cambio de batería",
			ServiceType.CHARGING_PORT, "Pin de carga",
			ServiceType.CAMERA, "Cámara",
			ServiceType.SOFTWARE_FLASHING, "Software",
			ServiceType.WATER_DAMAGE, "Daño por humedad",
			ServiceType.AUDIO, "Audio",
			ServiceType.GENERAL_DIAGNOSIS, "Diagnóstico general",
			ServiceType.OTHER, "Otro");

	static final Map<PaymentType, String> PAYMENT_TYPES = Map.of(
			PaymentType.DEPOSIT, "Abono",
			PaymentType.FINAL, "Cobro final",
			PaymentType.REFUND, "Devolución");

	static final Map<PaymentMethod, String> PAYMENT_METHODS = Map.of(
			PaymentMethod.CASH, "Efectivo",
			PaymentMethod.TRANSFER, "Transferencia",
			PaymentMethod.CARD, "Tarjeta");

	static final Map<RepairOutcome, String> OUTCOMES = Map.of(
			RepairOutcome.REPAIRED, "Reparado",
			RepairOutcome.NOT_REPAIRABLE, "No reparable",
			RepairOutcome.REJECTED_BY_CUSTOMER, "Rechazado por el cliente");

	static final Map<DeviceType, String> DEVICE_TYPES = Map.of(
			DeviceType.PHONE, "Teléfono",
			DeviceType.TABLET, "Tablet",
			DeviceType.SMARTWATCH, "Reloj inteligente",
			DeviceType.SPEAKER, "Parlante",
			DeviceType.OTHER, "Otro");

	static final Map<DocumentType, String> DOCUMENT_TYPES = Map.of(
			DocumentType.CC, "CC",
			DocumentType.TI, "TI",
			DocumentType.CE, "CE",
			DocumentType.PASSPORT, "Pasaporte",
			DocumentType.PPT, "PPT");

	private ReceiptLabels() {
	}
}
