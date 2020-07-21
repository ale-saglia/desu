package model;

import java.util.Set;

public class Account {
	String fiscalCode;
	String name;
	String numberVAT;
	String atecoCode;
	String legalAddress;
	String category;
	Set<Contact> contacts;

	public Account(String fiscalCode, String name, String numberVAT, String atecoCode, String legalAddress,
			String category) {
		this.fiscalCode = fiscalCode;
		this.name = name;
		this.numberVAT = numberVAT;
		this.atecoCode = atecoCode;
		this.legalAddress = legalAddress;
		this.category = category;
	}

	public String getFiscalCode() {
		return fiscalCode;
	}

	public String getName() {
		return name;
	}

	public String getNumberVAT() {
		return numberVAT;
	}

	public String getAtecoCode() {
		return atecoCode;
	}

	public String getLegalAddress() {
		return legalAddress;
	}

	public String getCategory() {
		return category;
	}

	private class Contact {
		// TODO enum description types
		String contact;

	}
}