package dclient.model;

import java.time.LocalDate;

public class Invoice implements Comparable<Invoice> {

	String id;
	Integer number;
	LocalDate emission;
	String type;
	Boolean payed;
	String description;

	public Invoice(String id, int number, LocalDate emission, String type, Boolean payed, String description) {
		this.id = id;
		this.number = number;
		this.emission = emission;
		this.type = type;
		this.payed = payed;
		this.description = description;
		
		trimInvoice();
	}

	public Invoice(Integer number, LocalDate emission, String type, Boolean payed, String description) {
		this.id = createID(number, type, emission);
		this.number = number;
		this.emission = emission;
		this.type = type;
		this.payed = payed;
		this.description = description;
		
		trimInvoice();
	}
	
	public Invoice(String number, LocalDate emission, String type, Boolean payed, String description) {
		try {
			this.number = Integer.parseInt(number.trim());
		} catch (NumberFormatException e) {
			this.number = null;
		}
		this.id = createID(this.number, type, emission);
		this.emission = emission;
		this.type = type;
		this.payed = payed;
		this.description = description;
		
	}
	
	private void trimInvoice() {
		if(id != null)
			id = id.trim();
		
		if(description != null)
			description = description.trim();
	}

	public String createID(Integer number, String type, LocalDate emission) {
		if (number == null || type == null || emission == null)
			return null;
		else
			return (number + "/" + type + " " + emission);
	}

	public String getId() {
		return id;
	}

	public Integer getNumber() {
		return number;
	}

	public LocalDate getEmission() {
		return emission;
	}

	public String getType() {
		return type;
	}

	public Boolean getPayed() {
		return payed;
	}

	public String getDescription() {
		return description;
	}

	@Override
	public int compareTo(Invoice o) {
		if (this.getEmission().getYear() == o.getEmission().getYear()) {
			if(this.getType().compareTo(o.getType()) == 0) {
				return (this.getNumber() - o.getNumber());
			} else
				return this.getType().compareTo(o.getType());
		} else if (this.getEmission().getYear() < o.getEmission().getYear())
			return -1;
		else
			return 1;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((emission == null) ? 0 : emission.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((number == null) ? 0 : number.hashCode());
		result = prime * result + ((payed == null) ? 0 : payed.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		Invoice other = (Invoice) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (emission == null) {
			if (other.emission != null)
				return false;
		} else if (!emission.equals(other.emission))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (number == null) {
			if (other.number != null)
				return false;
		} else if (!number.equals(other.number))
			return false;
		if (payed == null) {
			if (other.payed != null)
				return false;
		} else if (!payed.equals(other.payed))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}
	
	
}
