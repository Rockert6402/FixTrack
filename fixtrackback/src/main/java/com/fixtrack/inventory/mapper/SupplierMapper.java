package com.fixtrack.inventory.mapper;

import org.springframework.stereotype.Component;

import com.fixtrack.device.entity.DeviceModel;
import com.fixtrack.inventory.dto.CompatibleModelResponse;
import com.fixtrack.inventory.dto.SparePartSupplierResponse;
import com.fixtrack.inventory.dto.SupplierResponse;
import com.fixtrack.inventory.entity.Supplier;
import com.fixtrack.inventory.entity.SupplierSparePart;

@Component
public class SupplierMapper {

	public SupplierResponse toResponse(Supplier supplier) {
		return new SupplierResponse(supplier.getId(), supplier.getName(), supplier.getTaxId(),
				supplier.getContactName(), supplier.getPhone(), supplier.getEmail(), supplier.isActive());
	}

	public SparePartSupplierResponse toPartSupplier(SupplierSparePart link) {
		Supplier supplier = link.getSupplier();
		return new SparePartSupplierResponse(supplier.getId(), supplier.getName(), supplier.getTaxId(),
				supplier.isActive(), link.getSupplierSku(), link.getLastCost(), link.getLeadTimeDays(),
				link.isPreferred());
	}

	public CompatibleModelResponse toCompatibleModel(DeviceModel model) {
		return new CompatibleModelResponse(model.getId(), model.getBrand().getName(), model.getName(),
				model.getDeviceType());
	}
}
