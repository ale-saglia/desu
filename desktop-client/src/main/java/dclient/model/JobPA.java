package dclient.model;

import java.time.LocalDate;

public class JobPA extends Job {
	String cig;
	Integer decreeNumber;
	LocalDate decreeDate;

	public JobPA(String id, String jobCategory, String jobType, String description, Account customer) {
		super(id, jobCategory, jobType, description, customer);
		
		trimAccountString();
	}

	public JobPA(Job job, String cig, int decreeNumber, LocalDate decreeDate) {
		super(job.getId(), job.getJobCategory(), job.getJobType(), job.getDescription(), job.getCustomer());
		this.cig = cig;
		this.decreeNumber = decreeNumber;
		this.decreeDate = decreeDate;
		
		trimAccountString();
	}

	public JobPA(Job job, String cig, String decreeNumber, LocalDate decreeDate) {
		super(job.getId(), job.getJobCategory(), job.getJobType(), job.getDescription(), job.getCustomer());
		this.cig = cig;
		this.decreeDate = decreeDate;
		try {
			this.decreeNumber = Integer.parseInt(decreeNumber);
		} catch (NumberFormatException e) {
			this.decreeNumber = null;
		}
	}

	public String getCig() {
		return cig;
	}

	public Integer getDecreeNumber() {
		return decreeNumber;
	}

	public LocalDate getDecreeDate() {
		return decreeDate;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((cig == null) ? 0 : cig.hashCode());
		result = prime * result + ((decreeDate == null) ? 0 : decreeDate.hashCode());
		result = prime * result + decreeNumber;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		JobPA other = (JobPA) obj;
		if (cig == null) {
			if (other.cig != null)
				return false;
		} else if (!cig.equals(other.cig))
			return false;
		if (decreeDate == null) {
			if (other.decreeDate != null)
				return false;
		} else if (!decreeDate.equals(other.decreeDate))
			return false;
		if (decreeNumber != other.decreeNumber)
			return false;
		return true;
	}

	private void trimAccountString() {
		if(id != null)
			id = id.trim();
		
		if(description != null)
			description = description.trim();
		
		if(cig != null)
			cig = cig.trim();
	}
}
