package com.fixtrack.config;

import java.time.Clock;
import java.time.ZoneId;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Reloj del sistema en America/Bogota; se inyecta donde se calcule «hoy» para poder probarlo
@Configuration
public class ClockConfig {

	@Bean
	public Clock clock() {
		return Clock.system(ZoneId.of("America/Bogota"));
	}
}
