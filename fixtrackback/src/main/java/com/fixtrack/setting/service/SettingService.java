package com.fixtrack.setting.service;

import com.fixtrack.setting.entity.SettingKey;

public interface SettingService {

	int getIntValue(SettingKey key);

	String getValue(SettingKey key);
}
