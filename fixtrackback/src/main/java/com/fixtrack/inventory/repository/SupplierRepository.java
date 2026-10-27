package com.fixtrack.inventory.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fixtrack.inventory.entity.Supplier;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {

	Optional<Supplier> findByTaxId(String taxId);

	boolean existsByTaxId(String taxId);

	boolean existsByTaxIdAndIdNot(String taxId, Long id);

	// Búsqueda por nombre, NIT o contacto sin mayúsculas ni tildes; pattern llega con los comodines escapados
	@Query("""
			SELECT s FROM Supplier s
			WHERE s.active = :active
			  AND (:pattern IS NULL
			       OR collate(s.name as utf8mb4_0900_ai_ci) LIKE :pattern ESCAPE '!'
			       OR collate(s.taxId as utf8mb4_0900_ai_ci) LIKE :pattern ESCAPE '!'
			       OR collate(s.contactName as utf8mb4_0900_ai_ci) LIKE :pattern ESCAPE '!')
			""")
	Page<Supplier> search(@Param("pattern") String pattern, @Param("active") boolean active, Pageable pageable);
}
