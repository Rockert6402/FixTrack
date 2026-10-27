package com.fixtrack.setting.service;

import java.util.List;

import com.fixtrack.infra.security.AuthenticatedUser;
import com.fixtrack.setting.dto.SettingResponse;
import com.fixtrack.setting.dto.SettingUpdateRequest;

import com.fixtrack.setting.entity.SettingKey;

public interface SettingService {

	int getIntValue(SettingKey key);

	String getValue(SettingKey key);

	List<SettingResponse> findAll();

	SettingResponse findByKey(SettingKey key);

	SettingResponse update(SettingKey key, SettingUpdateRequest request, AuthenticatedUser actor);
}
