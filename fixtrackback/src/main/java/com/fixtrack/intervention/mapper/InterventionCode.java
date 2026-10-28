package com.fixtrack.intervention.mapper;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fixtrack.intervention.entity.Intervention;

// Código visible de la orden, calculado del id: FT-00001; las garantías agregan -G y su número (FT-00001-G1)
public final class InterventionCode {

	private static final Pattern CODE = Pattern.compile("^FT-(\\d+)(?:-G(\\d+))?$", Pattern.CASE_INSENSITIVE);
	private static final Pattern PARTIAL = Pattern.compile("^(?:FT?-?)?(\\d*)(?:-(?:G(\\d*))?)?$",
			Pattern.CASE_INSENSITIVE);

	private InterventionCode() {
	}

	public static String of(Intervention intervention) {
		if (intervention.getWarrantyOf() != null) {
			return format(intervention.getWarrantyOf().getId()) + "-G" + intervention.getWarrantyNumber();
		}
		return format(intervention.getId());
	}

	public static String format(Long id) {
		return String.format("FT-%05d", id);
	}

	// Convierte el código de vuelta a id (y número de garantía); acepta ceros de más como FT-000001
	public static Optional<Parsed> parse(String code) {
		if (code == null) {
			return Optional.empty();
		}
		Matcher matcher = CODE.matcher(code.trim());
		if (!matcher.matches()) {
			return Optional.empty();
		}
		try {
			Long baseId = Long.valueOf(matcher.group(1));
			Integer warrantyNumber = matcher.group(2) == null ? null : Integer.valueOf(matcher.group(2));
			return Optional.of(new Parsed(baseId, warrantyNumber));
		} catch (NumberFormatException e) {
			return Optional.empty();
		}
	}

	// Código a medio escribir en la búsqueda (F, FT-, FT-1, 00001, ft-1-g1); sirve si solo tiene esas partes
	public static boolean isPartial(String typed) {
		return typed != null && PARTIAL.matcher(typed.trim()).matches();
	}

	// Id (y garantía) del código a medio escribir; vacío mientras no tenga un número distinto de cero
	public static Optional<Parsed> parsePartial(String typed) {
		if (typed == null) {
			return Optional.empty();
		}
		Matcher matcher = PARTIAL.matcher(typed.trim());
		if (!matcher.matches() || matcher.group(1).isEmpty()) {
			return Optional.empty();
		}
		try {
			Long baseId = Long.valueOf(matcher.group(1));
			if (baseId == 0) {
				return Optional.empty();
			}
			String warranty = matcher.group(2);
			Integer warrantyNumber = warranty == null || warranty.isEmpty() ? null : Integer.valueOf(warranty);
			return Optional.of(new Parsed(baseId, warrantyNumber));
		} catch (NumberFormatException e) {
			return Optional.empty();
		}
	}

	public record Parsed(Long baseId, Integer warrantyNumber) {
	}
}
