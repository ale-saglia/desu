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
		this.number = number;
		this.emission = emission;
		this.type = type;
		this.payed = payed;
		createID();
	}

	public void createID() {
		this.id = (number + "/" + type + " " + emission);
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
}
