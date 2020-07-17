package model;

import java.time.LocalDate;

public class RSPP {
	Job job;
    Account account;
    
    LocalDate start;
    LocalDate end;
    
	public RSPP(Job job, LocalDate start, LocalDate end) {
		this.job = job;
		this.start = start;
		this.end = end;
	}
	
	
}