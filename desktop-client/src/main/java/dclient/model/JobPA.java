package dclient.model;

import java.time.LocalDate;

public class JobPA extends Job {
	String cig;
	int decreeNumber;
	LocalDate decreeDate;

	public JobPA(String id, String jobCategory, String jobType, String description, Account customer) {
		super(id, jobCategory, jobType, description, customer);
	}

	public JobPA(Job job, String cig, int decreeNumber, LocalDate decreeDate) {
		super(job.getId(), job.getJobCategory(), job.getJobType(), job.getDescription(), job.getCustomer());
		this.cig = cig;
		this.decreeNumber = decreeNumber;
		this.decreeDate = decreeDate;
	}

	public String getCig() {
		return cig;
	}

	public int getDecreeNumber() {
		return decreeNumber;
	}

	public LocalDate getDecreeDate() {
		return decreeDate;
	}

}
