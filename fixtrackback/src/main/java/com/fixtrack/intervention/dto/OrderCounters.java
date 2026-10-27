package com.fixtrack.intervention.dto;

// Contadores de órdenes calculados en una sola consulta agregada
public record OrderCounters(Long receivedToday, Long overdue, Long inProgress, Long waitingParts, Long ready,
		Long unclaimed, Long open) {
}
