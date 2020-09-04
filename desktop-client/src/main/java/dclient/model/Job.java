package dclient.model;

public class Job {
	String id;

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
		
		trimAccountString();
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((customer == null) ? 0 : customer.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((jobCategory == null) ? 0 : jobCategory.hashCode());
		result = prime * result + ((jobType == null) ? 0 : jobType.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Job other = (Job) obj;
		if (customer == null) {
			if (other.customer != null)
				return false;
		} else if (!customer.equals(other.customer))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (jobCategory == null) {
			if (other.jobCategory != null)
				return false;
		} else if (!jobCategory.equals(other.jobCategory))
			return false;
		if (jobType == null) {
			if (other.jobType != null)
				return false;
		} else if (!jobType.equals(other.jobType))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return id;
	}
	
	private void trimAccountString() {
		if(id != null)
			id = id.trim();
		
		if(description != null)
			description = description.trim();

	}

}