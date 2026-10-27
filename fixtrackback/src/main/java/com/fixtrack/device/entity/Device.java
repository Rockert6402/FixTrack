package com.fixtrack.device.entity;

import com.fixtrack.person.entity.Person;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Equipo de un cliente; el IMEI es obligatorio solo si el modelo es un teléfono
@Entity
@Table(name = "device", uniqueConstraints = @UniqueConstraint(name = "uk_device_imei", columnNames = "imei"))
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Device {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "owner_id", nullable = false)
	private Person owner;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "device_model_id", nullable = false)
	private DeviceModel deviceModel;

	@Column(name = "imei", length = 15)
	private String imei;

	@Column(name = "serial_number", length = 50)
	private String serialNumber;

	@Column(name = "color", length = 30)
	private String color;

	@Column(name = "observations", length = 500)
	private String observations;
}
