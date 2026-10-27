package com.fixtrack.inventory.service.impl;

import java.math.RoundingMode;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fixtrack.common.BusinessRuleException;
import com.fixtrack.common.ConflictException;
import com.fixtrack.common.NotFoundException;
import com.fixtrack.device.entity.DeviceModel;
import com.fixtrack.device.service.DeviceModelService;
import com.fixtrack.inventory.dto.CompatibilitiesRequest;
import com.fixtrack.inventory.dto.CompatibleModelResponse;
import com.fixtrack.inventory.dto.SparePartSupplierRequest;
import com.fixtrack.inventory.dto.SparePartSupplierResponse;
import com.fixtrack.inventory.dto.SparePartSuppliersRequest;
import com.fixtrack.inventory.entity.SparePart;
import com.fixtrack.inventory.entity.SparePartCompatibility;
import com.fixtrack.inventory.entity.Supplier;
import com.fixtrack.inventory.entity.SupplierSparePart;
import com.fixtrack.inventory.mapper.SupplierMapper;
import com.fixtrack.inventory.repository.SparePartCompatibilityRepository;
import com.fixtrack.inventory.repository.SparePartRepository;
import com.fixtrack.inventory.repository.SupplierRepository;
import com.fixtrack.inventory.repository.SupplierSparePartRepository;
import com.fixtrack.inventory.service.SparePartLinkService;

import lombok.RequiredArgsConstructor;

// Proveedores (código, último costo, días de entrega y preferido) y modelos compatibles de cada repuesto
@Service
@RequiredArgsConstructor
public class SparePartLinkServiceImpl implements SparePartLinkService {

	private static final Comparator<SparePartSupplierResponse> PREFERRED_FIRST = Comparator
			.comparing(SparePartSupplierResponse::preferred).reversed()
			.thenComparing(SparePartSupplierResponse::supplierName, String.CASE_INSENSITIVE_ORDER);

	private final SparePartRepository sparePartRepository;
	private final SupplierRepository supplierRepository;
	private final SupplierSparePartRepository supplierSparePartRepository;
	private final SparePartCompatibilityRepository compatibilityRepository;
	private final DeviceModelService deviceModelService;
	private final SupplierMapper supplierMapper;

	@Override
	@Transactional(readOnly = true)
	public List<SparePartSupplierResponse> findSuppliers(Long sparePartId) {
		findPart(sparePartId);
		return supplierSparePartRepository.findBySparePartId(sparePartId).stream()
				.map(supplierMapper::toPartSupplier)
				.sorted(PREFERRED_FIRST)
				.toList();
	}

	// Reemplaza la lista: quita los que no vienen, actualiza los que siguen y agrega los nuevos
	@Override
	@Transactional
	public List<SparePartSupplierResponse> replaceSuppliers(Long sparePartId, SparePartSuppliersRequest request) {
		SparePart part = findPart(sparePartId);
		List<SparePartSupplierRequest> items = request.suppliers();

		// 1. Un proveedor una sola vez y como máximo un preferido
		Set<Long> seen = new HashSet<>();
		for (SparePartSupplierRequest item : items) {
			if (!seen.add(item.supplierId())) {
				throw new BusinessRuleException("El proveedor con id " + item.supplierId() + " está repetido");
			}
		}
		if (items.stream().filter(item -> Boolean.TRUE.equals(item.preferred())).count() > 1) {
			throw new BusinessRuleException("Solo un proveedor puede ser el preferido");
		}

		// 2. Los vínculos actuales; un proveedor dado de baja solo puede seguir si ya estaba
		Map<Long, SupplierSparePart> current = supplierSparePartRepository.findBySparePartId(sparePartId).stream()
				.collect(Collectors.toMap(link -> link.getSupplier().getId(), Function.identity()));
		for (SparePartSupplierRequest item : items) {
			SupplierSparePart link = current.remove(item.supplierId());
			if (link == null) {
				Supplier supplier = supplierRepository.findById(item.supplierId())
						.orElseThrow(() -> new NotFoundException("No existe el proveedor con id " + item.supplierId()));
				if (!supplier.isActive()) {
					throw new ConflictException("El proveedor " + supplier.getName() + " está dado de baja");
				}
				link = supplierSparePartRepository.save(new SupplierSparePart(supplier, part));
			}
			link.setSupplierSku(item.supplierSku() == null || item.supplierSku().isBlank() ? null : item.supplierSku().trim());
			// Sin lastCost se conserva el que había (lo actualiza la recepción de las solicitudes)
			if (item.lastCost() != null) {
				link.setLastCost(item.lastCost().setScale(2, RoundingMode.HALF_UP));
			}
			link.setLeadTimeDays(item.leadTimeDays());
			link.setPreferred(Boolean.TRUE.equals(item.preferred()));
		}

		// 3. Los que no vinieron se quitan
		supplierSparePartRepository.deleteAll(current.values());
		supplierSparePartRepository.flush();
		return findSuppliers(sparePartId);
	}

	@Override
	@Transactional(readOnly = true)
	public List<CompatibleModelResponse> findCompatibilities(Long sparePartId) {
		findPart(sparePartId);
		return compatibilityRepository.findBySparePartId(sparePartId).stream()
				.map(compatibility -> supplierMapper.toCompatibleModel(compatibility.getDeviceModel()))
				.sorted(Comparator.comparing(CompatibleModelResponse::brandName)
						.thenComparing(CompatibleModelResponse::name))
				.toList();
	}

	// El modelo principal ya es compatible por definición, así que no se repite en la lista
	@Override
	@Transactional
	public List<CompatibleModelResponse> replaceCompatibilities(Long sparePartId, CompatibilitiesRequest request) {
		SparePart part = findPart(sparePartId);
		Set<Long> requested = new LinkedHashSet<>(request.deviceModelIds());
		if (requested.contains(part.getDeviceModel().getId())) {
			throw new BusinessRuleException("El modelo principal del repuesto (" + part.getDeviceModel().getName()
					+ ") no se agrega como compatible");
		}

		List<SparePartCompatibility> current = compatibilityRepository.findBySparePartId(sparePartId);
		Set<Long> kept = new HashSet<>();
		for (SparePartCompatibility compatibility : current) {
			if (requested.contains(compatibility.getDeviceModel().getId())) {
				kept.add(compatibility.getDeviceModel().getId());
			} else {
				compatibilityRepository.delete(compatibility);
			}
		}
		for (Long modelId : requested) {
			if (!kept.contains(modelId)) {
				DeviceModel model = deviceModelService.getEntity(modelId);
				compatibilityRepository.save(new SparePartCompatibility(part, model));
			}
		}
		compatibilityRepository.flush();
		return findCompatibilities(sparePartId);
	}

	private SparePart findPart(Long id) {
		return sparePartRepository.findByIdAndActiveTrue(id)
				.orElseThrow(() -> new NotFoundException("No existe el repuesto con id " + id));
	}
}
