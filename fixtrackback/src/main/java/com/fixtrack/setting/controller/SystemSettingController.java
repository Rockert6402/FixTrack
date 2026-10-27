package com.fixtrack.setting.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fixtrack.infra.security.CurrentUserProvider;
import com.fixtrack.setting.dto.SettingResponse;
import com.fixtrack.setting.dto.SettingUpdateRequest;
import com.fixtrack.setting.entity.SettingKey;
import com.fixtrack.setting.service.SettingService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/settings")
@RequiredArgsConstructor
public class SystemSettingController {

	private final SettingService settingService;
	private final CurrentUserProvider currentUserProvider;

	@GetMapping
	@PreAuthorize("hasRole('ADMIN')")
	public List<SettingResponse> list() {
		return settingService.findAll();
	}

	@GetMapping("/{key}")
	@PreAuthorize("hasRole('ADMIN')")
	public SettingResponse get(@PathVariable SettingKey key) {
		return settingService.findByKey(key);
	}

	@PutMapping("/{key}")
	@PreAuthorize("hasRole('ADMIN')")
	public SettingResponse update(@PathVariable SettingKey key, @Valid @RequestBody SettingUpdateRequest request) {
		return settingService.update(key, request, currentUserProvider.require());
	}
}
