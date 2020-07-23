package dclient.model;

import java.time.LocalDate;

public class RSPP {
	Job job;
	Invoice invoice;

	LocalDate start;
	LocalDate end;

	public RSPP(Job job, LocalDate start, LocalDate end, Invoice invoice) {
		this.job = job;
		this.start = start;
		this.end = end;
		this.invoice = invoice;
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

	public Invoice getInvoice() {
		return invoice;
	}

	
}