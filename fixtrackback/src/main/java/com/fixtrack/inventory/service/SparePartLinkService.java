package com.fixtrack.inventory.service;

import java.util.List;

import com.fixtrack.inventory.dto.CompatibilitiesRequest;
import com.fixtrack.inventory.dto.CompatibleModelResponse;
import com.fixtrack.inventory.dto.SparePartSupplierResponse;
import com.fixtrack.inventory.dto.SparePartSuppliersRequest;

// Proveedores y modelos compatibles de un repuesto; cada PUT reemplaza la lista completa
public interface SparePartLinkService {

	List<SparePartSupplierResponse> findSuppliers(Long sparePartId);

	List<SparePartSupplierResponse> replaceSuppliers(Long sparePartId, SparePartSuppliersRequest request);

	List<CompatibleModelResponse> findCompatibilities(Long sparePartId);

	List<CompatibleModelResponse> replaceCompatibilities(Long sparePartId, CompatibilitiesRequest request);
}
