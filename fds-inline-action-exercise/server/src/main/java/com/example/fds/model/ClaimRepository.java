package com.example.fds.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The claims, in memory. A database in the real platform.
 */
public class ClaimRepository {

	public ClaimRepository() {
		_add("CLM-1001", "R. Ferreira", "Client dinner, Lisbon", 84);
		_add("CLM-1002", "M. Okafor", "Taxi to airport", 41);
		_add("CLM-1003", "J. Lindqvist", "Conference ticket", 1450);
		_add("CLM-1004", "P. Nowak", "Hotel, two nights", 320);
		_add("CLM-1005", "S. Bhatt", "Team offsite catering", 2100);
		_add("CLM-1006", "A. Delacroix", "Monitor stand", 62);
		_add("CLM-1007", "K. Yamamoto", "Rail fare", 118);
		_add("CLM-1008", "L. Moreau", "Client lunch", 96);
		_add("CLM-1009", "T. Andersson", "Annual licence renewal", 3400);
		_add("CLM-1010", "D. Fernandes", "Parking", 22);
		_add("CLM-1011", "H. Oyelaran", "Workshop materials", 205);
		_add("CLM-1012", "E. Vargas", "Flight change fee", 175);
	}

	public Claim getClaim(String id) {
		return _claims.get(id);
	}

	public synchronized List<Claim> getClaims(String status) {
		List<Claim> claims = new ArrayList<>();

		for (Claim claim : _claims.values()) {
			if ((status == null) || status.isBlank() ||
				status.equals(claim.getStatus())) {

				claims.add(claim);
			}
		}

		return claims;
	}

	private void _add(
		String id, String employee, String description, int amount) {

		_claims.put(
			id,
			new Claim(
				id, employee, description, amount, Claim.PENDING,
				"2026-08-" + (10 + _claims.size())));
	}

	private final Map<String, Claim> _claims = new LinkedHashMap<>();

}
