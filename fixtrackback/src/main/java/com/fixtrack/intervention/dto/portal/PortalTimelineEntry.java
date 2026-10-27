package com.fixtrack.intervention.dto.portal;

import java.time.LocalDateTime;

import com.fixtrack.intervention.entity.InterventionStatus;

public record PortalTimelineEntry(InterventionStatus status, LocalDateTime date) {
}
