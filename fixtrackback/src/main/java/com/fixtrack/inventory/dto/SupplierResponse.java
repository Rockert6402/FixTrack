package com.fixtrack.inventory.dto;

public record SupplierResponse(Long id, String name, String taxId, String contactName, String phone, String email,
		boolean active) {
}
