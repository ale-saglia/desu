package dclient.model;

import java.time.LocalDate;

public class JobPA extends Job {
	String cig;
	int decreeNumber;
	LocalDate decreeDate;
	
	public JobPA(String id, String jobCategory, String jobType, String description, Account customer) {
		super(id, jobCategory, jobType, description, customer);
	}

}
