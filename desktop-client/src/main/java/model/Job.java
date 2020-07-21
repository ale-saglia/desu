package model;

public class Job {
	String id;

	enum jobCategory {
		// TODO create autofetch of enum from database
		SICUREZZA;
	};

	enum jobType {
		// TODO create autofetch of enum from database
		RSPP;
	};

	String description;
	Account customer;
	private String jobCategory;
	private String jobType;

	public Job(String id, String jobCategory, String jobType, String description, Account customer) {
		this.id = id;
		this.description = description;
		this.jobCategory = jobCategory;
		this.jobType = jobType;
		this.customer = customer;
	}

	public String getId() {
		return id;
	}

	public String getDescription() {
		return description;
	}

	public Account getCustomer() {
		return customer;
	}

	public String getJobCategory() {
		return jobCategory;
	}

	public String getJobType() {
		return jobType;
	}

}