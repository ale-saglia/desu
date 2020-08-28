package dclient.model;

import java.time.LocalDate;

public class Invoice {

	String id;
	Integer number;
	LocalDate emission;
	String type;
	Boolean payed;

	public Invoice(String id, int number, LocalDate emission, String type, Boolean payed) {
		this.id = id;
		this.number = number;
		this.emission = emission;
		this.type = type;
		this.payed = payed;
	}

	public Invoice(Integer number, LocalDate emission, String type, Boolean payed) {
		this.id = createID(number, type, emission);
		this.number = number;
		this.emission = emission;
		this.type = type;
		this.payed = payed;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
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
