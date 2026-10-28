package com.fixtrack.intervention.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class InterventionCodeTest {

	@Test
	void shouldParsePartialCodesWhileTyping() {
		assertThat(InterventionCode.parsePartial("FT-00001")).contains(new InterventionCode.Parsed(1L, null));
		assertThat(InterventionCode.parsePartial("ft-12")).contains(new InterventionCode.Parsed(12L, null));
		assertThat(InterventionCode.parsePartial("00007")).contains(new InterventionCode.Parsed(7L, null));
		assertThat(InterventionCode.parsePartial("FT-00001-G2")).contains(new InterventionCode.Parsed(1L, 2));
		assertThat(InterventionCode.parsePartial("FT-00001-G")).contains(new InterventionCode.Parsed(1L, null));
	}

	@Test
	void shouldNotFilterUntilTheCodeHasANumber() {
		for (String typed : new String[] { "F", "FT", "FT-", "FT-000" }) {
			assertThat(InterventionCode.isPartial(typed)).isTrue();
			assertThat(InterventionCode.parsePartial(typed)).isEmpty();
		}
	}

	@Test
	void shouldRejectTextThatIsNotACode() {
		assertThat(InterventionCode.isPartial("ABC")).isFalse();
		assertThat(InterventionCode.isPartial("FT-1X")).isFalse();
	}
}
