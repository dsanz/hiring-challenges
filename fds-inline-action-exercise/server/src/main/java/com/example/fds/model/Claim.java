package com.example.fds.model;

/**
 * An expense claim.
 */
public class Claim {

	public static final String APPROVED = "Approved";

	public static final String PENDING = "Pending";

	public static final String REJECTED = "Rejected";

	public Claim(
		String id, String employee, String description, int amount,
		String status, String submittedOn) {

		_id = id;
		_employee = employee;
		_description = description;
		_amount = amount;
		_status = status;
		_submittedOn = submittedOn;
		_version = 1;
	}

	public int getAmount() {
		return _amount;
	}

	public String getDescription() {
		return _description;
	}

	public String getEmployee() {
		return _employee;
	}

	public String getId() {
		return _id;
	}

	public String getStatus() {
		return _status;
	}

	public String getSubmittedOn() {
		return _submittedOn;
	}

	public int getVersion() {
		return _version;
	}

	public void setStatus(String status) {
		_status = status;

		_version++;
	}

	private final int _amount;
	private final String _description;
	private final String _employee;
	private final String _id;
	private String _status;
	private final String _submittedOn;
	private int _version;

}
