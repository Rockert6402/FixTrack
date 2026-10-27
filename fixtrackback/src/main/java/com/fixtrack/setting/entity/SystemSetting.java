package com.fixtrack.setting.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.UpdateTimestamp;

import com.fixtrack.person.entity.Person;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
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

// Parámetros del sistema que edita el administrador (stock mínimo, días de garantía, condiciones del servicio)
@Entity
@Table(name = "system_setting")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SystemSetting {

	@Id
	@Enumerated(EnumType.STRING)
	@Column(name = "setting_key", length = 50)
	private SettingKey settingKey;

	@Column(name = "setting_value", nullable = false, columnDefinition = "TEXT")
	private String settingValue;

	// null cuando el valor lo cargó el sistema
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "updated_by_id")
	private Person updatedBy;

	@UpdateTimestamp
	@Column(name = "updated_at")
	private LocalDateTime updatedAt;
}
