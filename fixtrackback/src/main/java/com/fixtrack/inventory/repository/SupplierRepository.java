package com.fixtrack.inventory.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fixtrack.inventory.entity.Supplier;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {

	Optional<Supplier> findByTaxId(String taxId);

	boolean existsByTaxId(String taxId);

	boolean existsByTaxIdAndIdNot(String taxId, Long id);
}
