package com.fixtrack.intervention.event;

// Se publica dentro de la transacción de la cancelación; otros dominios reaccionan sin que la orden dependa de ellos
public record InterventionCancelledEvent(Long interventionId, Long cancelledById) {
}
