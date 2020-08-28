package dclient.model;

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
		// String contact;

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((atecoCode == null) ? 0 : atecoCode.hashCode());
		result = prime * result + ((category == null) ? 0 : category.hashCode());
		result = prime * result + ((fiscalCode == null) ? 0 : fiscalCode.hashCode());
		result = prime * result + ((legalAddress == null) ? 0 : legalAddress.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((numberVAT == null) ? 0 : numberVAT.hashCode());
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
		Account other = (Account) obj;
		if (atecoCode == null) {
			if (other.atecoCode != null)
				return false;
		} else if (!atecoCode.equals(other.atecoCode))
			return false;
		if (category == null) {
			if (other.category != null)
				return false;
		} else if (!category.equals(other.category))
			return false;
		if (fiscalCode == null) {
			if (other.fiscalCode != null)
				return false;
		} else if (!fiscalCode.equals(other.fiscalCode))
			return false;
		if (legalAddress == null) {
			if (other.legalAddress != null)
				return false;
		} else if (!legalAddress.equals(other.legalAddress))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (numberVAT == null) {
			if (other.numberVAT != null)
				return false;
		} else if (!numberVAT.equals(other.numberVAT))
			return false;
		return true;
	}
	
	
}