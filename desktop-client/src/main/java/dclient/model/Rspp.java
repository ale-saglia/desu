package dclient.model;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Set;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class Rspp {
	Job job;
	Collection<Invoice> invoices;

	LocalDate start;
	LocalDate end;

	public Rspp(Job job, LocalDate start, LocalDate end, Set<Invoice> invoices) {
		this.job = job;
		this.start = start;
		this.end = end;
		this.invoices = invoices;
	}
	
	public Rspp(Job job, LocalDate start, LocalDate end) {
		this.job = job;
		this.start = start;
		this.end = end;
		this.invoices = null;
	}
	
	public void setInvoice(Collection<Invoice> invoices) {
		this.invoices = invoices;
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

	public Collection<Invoice> getInvoices() {
		return invoices;
	}
	
	public BiMap<String, Invoice> getInvoiceMap(){
		BiMap<String, Invoice> invoiceMap = HashBiMap.create();
		
		for(Invoice invoice : invoices)
			invoiceMap.put(invoice.getId(), invoice);
		
		return invoiceMap;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((end == null) ? 0 : end.hashCode());
		result = prime * result + ((job == null) ? 0 : job.hashCode());
		result = prime * result + ((start == null) ? 0 : start.hashCode());
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
		Rspp other = (Rspp) obj;
		if (end == null) {
			if (other.end != null)
				return false;
		} else if (!end.equals(other.end))
			return false;
		if (job == null) {
			if (other.job != null)
				return false;
		} else if (!job.equals(other.job))
			return false;
		if (start == null) {
			if (other.start != null)
				return false;
		} else if (!start.equals(other.start))
			return false;
		return true;
	}

	
}