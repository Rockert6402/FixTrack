package com.fixtrack.setting.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fixtrack.common.BusinessRuleException;
import com.fixtrack.infra.security.AuthenticatedUser;
import com.fixtrack.person.service.PersonService;
import com.fixtrack.setting.dto.SettingResponse;
import com.fixtrack.setting.dto.SettingUpdateRequest;
import com.fixtrack.setting.entity.SettingKey;
import com.fixtrack.setting.entity.SettingType;
import com.fixtrack.setting.entity.SystemSetting;
import com.fixtrack.setting.repository.SystemSettingRepository;
import com.fixtrack.setting.service.SettingService;

import lombok.RequiredArgsConstructor;

// Lee los parámetros del sistema (o su valor por defecto) y guarda los que cambia el administrador, validando su rango
@Service
@RequiredArgsConstructor
public class SettingServiceImpl implements SettingService {

	private final SystemSettingRepository systemSettingRepository;
	private final PersonService personService;

	@Override
	@Transactional(readOnly = true)
	public int getIntValue(SettingKey key) {
		return Integer.parseInt(getValue(key).trim());
	}

	@Override
	@Transactional(readOnly = true)
	public String getValue(SettingKey key) {
		return systemSettingRepository.findById(key)
				.map(SystemSetting::getSettingValue)
				.orElse(key.getDefaultValue());
	}

	@Override
	@Transactional(readOnly = true)
	public List<SettingResponse> findAll() {
		Map<SettingKey, SystemSetting> stored = systemSettingRepository.findAllWithUpdater().stream()
				.collect(Collectors.toMap(SystemSetting::getSettingKey, Function.identity()));
		return Arrays.stream(SettingKey.values()).map(key -> toResponse(key, stored.get(key))).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public SettingResponse findByKey(SettingKey key) {
		return toResponse(key, systemSettingRepository.findById(key).orElse(null));
	}

	// Guarda el valor ya normalizado; los enteros deben estar en el rango del parámetro
	@Override
	@Transactional
	public SettingResponse update(SettingKey key, SettingUpdateRequest request, AuthenticatedUser actor) {
		String value = normalize(key, request.value());
		SystemSetting setting = systemSettingRepository.findById(key)
				.orElseGet(() -> SystemSetting.builder().settingKey(key).build());
		setting.setSettingValue(value);
		setting.setUpdatedBy(personService.getEntity(actor.id()));
		systemSettingRepository.saveAndFlush(setting);
		return toResponse(key, setting);
	}

	private String normalize(SettingKey key, String raw) {
		if (key.getType() == SettingType.INTEGER) {
			int number;
			try {
				number = Integer.parseInt(raw.trim());
			} catch (NumberFormatException e) {
				throw new BusinessRuleException(key + " debe ser un número entero");
			}
			if (number < key.getMin() || number > key.getMax()) {
				throw new BusinessRuleException(key + " debe estar entre " + key.getMin() + " y " + key.getMax());
			}
			return String.valueOf(number);
		}
		String text = raw.replace("\r\n", "\n").strip();
		if (text.length() > key.getMax()) {
			throw new BusinessRuleException(key + " admite máximo " + key.getMax() + " caracteres");
		}
		return text;
	}

	private SettingResponse toResponse(SettingKey key, SystemSetting stored) {
		return new SettingResponse(key, stored == null ? key.getDefaultValue() : stored.getSettingValue(),
				key.getDefaultValue(), stored != null, key.getDescription(), key.getType(), key.getMin(), key.getMax(),
				stored == null || stored.getUpdatedBy() == null ? null : stored.getUpdatedBy().getFullName(),
				stored == null ? null : stored.getUpdatedAt());
	}
}
