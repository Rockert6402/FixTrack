package com.fixtrack.inventory.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.fixtrack.inventory.entity.SupplierSparePart;
import com.fixtrack.inventory.entity.SupplierSparePartId;

public interface SupplierSparePartRepository extends JpaRepository<SupplierSparePart, SupplierSparePartId> {

	@EntityGraph(attributePaths = { "supplier" })
	List<SupplierSparePart> findBySparePartId(Long sparePartId);

	// Proveedores preferidos de varios repuestos, para sugerirlos en las solicitudes
	@EntityGraph(attributePaths = { "supplier" })
	List<SupplierSparePart> findBySparePartIdInAndPreferredTrue(Collection<Long> sparePartIds);
}
