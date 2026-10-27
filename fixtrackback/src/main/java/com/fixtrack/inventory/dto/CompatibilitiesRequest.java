package com.fixtrack.inventory.dto;

import java.util.List;

import jakarta.validation.constraints.NotNull;

// Modelos compatibles además del principal: reemplaza la lista anterior; vacía los quita todos
public record CompatibilitiesRequest(@NotNull List<@NotNull Long> deviceModelIds) {
}
