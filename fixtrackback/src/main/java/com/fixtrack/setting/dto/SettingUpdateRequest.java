package com.fixtrack.setting.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SettingUpdateRequest(@NotNull @Size(max = 5000) String value) {
}
