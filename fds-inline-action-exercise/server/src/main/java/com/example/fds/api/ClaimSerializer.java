package com.example.fds.api;

import com.example.fds.model.Claim;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The wire shape of a claim.
 */
public class ClaimSerializer {

	public static Map<String, Object> toMap(Claim claim) {
		Map<String, Object> map = new LinkedHashMap<>();

		map.put("amount", claim.getAmount());
		map.put("description", claim.getDescription());
		map.put("employee", claim.getEmployee());
		map.put("id", claim.getId());
		map.put("status", claim.getStatus());
		map.put("submittedOn", claim.getSubmittedOn());
		map.put("version", claim.getVersion());

		return map;
	}

}
