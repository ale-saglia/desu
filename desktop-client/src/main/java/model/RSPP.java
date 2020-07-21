package model;

import java.time.LocalDate;

public class RSPP {
	Job job;

    LocalDate start;
    LocalDate end;
    
	public RSPP(Job job, LocalDate start, LocalDate end) {
		this.job = job;
		this.start = start;
		this.end = end;
	}

	public Job getJob() {
		return job;
	}

	public LocalDate getStart() {
		return start;
	}

	public LocalDate getEnd() {
		return end;
	}
	
	
}