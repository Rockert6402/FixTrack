package com.fixtrack.intervention.dto.report;

import com.fixtrack.intervention.entity.InterventionStatus;

public record StatusCount(InterventionStatus status, Long count) {
}
