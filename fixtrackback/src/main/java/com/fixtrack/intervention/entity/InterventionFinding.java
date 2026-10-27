package com.fixtrack.intervention.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

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

// Daño reportado en una orden: el inicial o uno adicional que el cliente debe aprobar
@Entity
@Table(name = "intervention_finding")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class InterventionFinding {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "intervention_id", nullable = false)
	private Intervention intervention;

	@Enumerated(EnumType.STRING)
	@Column(name = "service_type", nullable = false, length = 30)
	private ServiceType serviceType;

	@Column(name = "description", nullable = false, length = 1000)
	private String description;

	@Enumerated(EnumType.STRING)
	@Column(name = "origin", nullable = false, length = 20)
	private FindingOrigin origin;

	@Enumerated(EnumType.STRING)
	@Column(name = "approval_status", nullable = false, length = 20)
	private ApprovalStatus approvalStatus;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "reported_by_id", nullable = false)
	private Person reportedBy;

	// Asesor que registró la respuesta del cliente; null si el daño se aprobó solo por estar dentro del presupuesto
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "responded_by_id")
	private Person respondedBy;

	@Column(name = "responded_at")
	private LocalDateTime respondedAt;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;
}
