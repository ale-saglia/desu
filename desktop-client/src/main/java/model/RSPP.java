package model;

import java.util.Date;

public class RSPP {
	Job job;
    Account account;
    
    Date start;
    Date end;
	public RSPP(Job job, Date start, Date end) {
		this.job = job;
		this.start = start;
		this.end = end;
	}
}