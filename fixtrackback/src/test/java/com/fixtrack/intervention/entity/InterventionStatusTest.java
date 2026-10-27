package com.fixtrack.intervention.entity;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class InterventionStatusTest {

	@Test
	void shouldFollowRepairFlowWithoutOutcome() {
		assertThat(InterventionStatus.IN_DIAGNOSIS.canChangeTo(InterventionStatus.IN_REPAIR, null)).isTrue();
		assertThat(InterventionStatus.IN_DIAGNOSIS.canChangeTo(InterventionStatus.READY, null)).isFalse();
		assertThat(InterventionStatus.IN_REPAIR.canChangeTo(InterventionStatus.READY, RepairOutcome.REPAIRED)).isTrue();
	}

	@Test
	void shouldOnlyAllowReadyWhenOrderEndsWithoutRepair() {
		for (RepairOutcome outcome : new RepairOutcome[] { RepairOutcome.NOT_REPAIRABLE,
				RepairOutcome.REJECTED_BY_CUSTOMER }) {
			assertThat(InterventionStatus.IN_DIAGNOSIS.canChangeTo(InterventionStatus.READY, outcome)).isTrue();
			assertThat(InterventionStatus.WAITING_PARTS.canChangeTo(InterventionStatus.READY, outcome)).isTrue();
			assertThat(InterventionStatus.IN_DIAGNOSIS.canChangeTo(InterventionStatus.IN_REPAIR, outcome)).isFalse();
			assertThat(InterventionStatus.WAITING_PARTS.canChangeTo(InterventionStatus.IN_REPAIR, outcome)).isFalse();
			assertThat(InterventionStatus.READY.canChangeTo(InterventionStatus.IN_REPAIR, outcome)).isFalse();
		}
	}

	@Test
	void shouldKeepDeliveryFromReadyRegardlessOfOutcome() {
		assertThat(InterventionStatus.READY.canChangeTo(InterventionStatus.DELIVERED, RepairOutcome.REPAIRED)).isTrue();
		assertThat(RepairOutcome.REPAIRED.endsWithoutRepair()).isFalse();
		assertThat(RepairOutcome.NOT_REPAIRABLE.endsWithoutRepair()).isTrue();
	}
}
