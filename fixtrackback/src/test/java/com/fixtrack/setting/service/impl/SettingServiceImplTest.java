package com.fixtrack.setting.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fixtrack.common.BusinessRuleException;
import com.fixtrack.infra.security.AuthenticatedUser;
import com.fixtrack.person.entity.Person;
import com.fixtrack.person.entity.Role;
import com.fixtrack.person.service.PersonService;
import com.fixtrack.setting.dto.SettingResponse;
import com.fixtrack.setting.dto.SettingUpdateRequest;
import com.fixtrack.setting.entity.SettingKey;
import com.fixtrack.setting.entity.SystemSetting;
import com.fixtrack.setting.repository.SystemSettingRepository;

@ExtendWith(MockitoExtension.class)
class SettingServiceImplTest {

	private static final AuthenticatedUser ADMIN = new AuthenticatedUser(1L, "Admin", "admin@fixtrack.test",
			Role.ADMIN, "hash", true);

	@Mock
	private SystemSettingRepository systemSettingRepository;

	@Mock
	private PersonService personService;

	@InjectMocks
	private SettingServiceImpl service;

	@Test
	void shouldListEveryKeyWithDefaults() {
		given(systemSettingRepository.findAllWithUpdater()).willReturn(List.of());

		List<SettingResponse> settings = service.findAll();

		assertThat(settings).extracting(SettingResponse::key).containsExactly(SettingKey.values());
		assertThat(settings).allMatch(setting -> !setting.customized());
	}

	@Test
	void shouldRejectIntegerOutOfRange() {
		assertThatThrownBy(() -> service.update(SettingKey.UNCLAIMED_ALERT_DAYS, new SettingUpdateRequest("0"), ADMIN))
				.isInstanceOf(BusinessRuleException.class)
				.hasMessageContaining("entre 1 y 3650");
		assertThatThrownBy(() -> service.update(SettingKey.DEFAULT_MIN_STOCK, new SettingUpdateRequest("dos"), ADMIN))
				.isInstanceOf(BusinessRuleException.class)
				.hasMessageContaining("entero");
		verify(systemSettingRepository, never()).saveAndFlush(any());
	}

	@Test
	void shouldStoreNormalizedValueAndWhoChangedIt() {
		given(systemSettingRepository.findById(SettingKey.DEFAULT_WARRANTY_DAYS)).willReturn(Optional.empty());
		given(personService.getEntity(1L)).willReturn(Person.builder().id(1L).fullName("Admin").build());

		SettingResponse updated = service.update(SettingKey.DEFAULT_WARRANTY_DAYS, new SettingUpdateRequest(" 060 "),
				ADMIN);

		assertThat(updated.value()).isEqualTo("60");
		assertThat(updated.customized()).isTrue();
		assertThat(updated.updatedByName()).isEqualTo("Admin");
		verify(systemSettingRepository).saveAndFlush(any(SystemSetting.class));
	}
}
