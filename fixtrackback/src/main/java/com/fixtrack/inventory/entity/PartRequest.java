package com.fixtrack.inventory.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.fixtrack.intervention.entity.Intervention;
import com.fixtrack.person.entity.Person;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Solicitud de un repuesto que no hay en inventario, hecha por el técnico para una orden
@Entity
@Table(name = "part_request")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PartRequest {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "intervention_id", nullable = false)
	private Intervention intervention;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "requested_by_id", nullable = false)
	private Person requestedBy;

	// null cuando el repuesto todavía no existe en el catálogo
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "spare_part_id")
	private SparePart sparePart;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "supplier_id")
	private Supplier supplier;

	@Column(name = "description", nullable = false, length = 500)
	private String description;

	@Column(name = "quantity", nullable = false)
	private int quantity;

	@Builder.Default
	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 20)
	private PartRequestStatus status = PartRequestStatus.PENDING;

	@Column(name = "estimated_cost", precision = 12, scale = 2)
	private BigDecimal estimatedCost;

	@Column(name = "actual_cost", precision = 12, scale = 2)
	private BigDecimal actualCost;

	@Column(name = "rejection_reason", length = 500)
	private String rejectionReason;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "handled_by_id")
	private Person handledBy;

	@CreationTimestamp
	@Column(name = "requested_at", nullable = false, updatable = false)
	private LocalDateTime requestedAt;

	@Column(name = "received_at")
	private LocalDateTime receivedAt;
}
