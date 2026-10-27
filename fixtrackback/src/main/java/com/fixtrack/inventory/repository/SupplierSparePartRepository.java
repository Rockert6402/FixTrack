package com.fixtrack.inventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fixtrack.inventory.entity.SupplierSparePart;
import com.fixtrack.inventory.entity.SupplierSparePartId;

public interface SupplierSparePartRepository extends JpaRepository<SupplierSparePart, SupplierSparePartId> {
}
