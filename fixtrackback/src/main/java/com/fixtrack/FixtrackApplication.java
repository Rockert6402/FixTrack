package com.fixtrack;

import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class FixtrackApplication {

	public static void main(String[] args) {
		// Fija la zona horaria de la JVM antes de que arranque Spring
		TimeZone.setDefault(TimeZone.getTimeZone("America/Bogota"));
		SpringApplication.run(FixtrackApplication.class, args);
	}

}
