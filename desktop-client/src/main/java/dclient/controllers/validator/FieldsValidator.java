package dclient.controllers.validator;

import dclient.model.*;

public class FieldsValidator {

	public static String isNewAccountDuplicate(Model model, Account account) {
		String error = "";

		if (model.isAccountFiscalCodeExisting(account.getFiscalCode()))
			error += "- Il codice fiscale è già presente nel database per " + model.getAccount(account.getFiscalCode()).getName() + "\n";

		if(model.isAccountVatNumberExisting(account.getNumberVAT()))
			error += "- La partita IVA è già presente nel database per " + model.getAccount(account.getFiscalCode()).getName() + "\n";
		
		if (error.isEmpty())
			return null;
		else
			return error.trim();
	}

	public static String isNewInvoiceDuplicate(Model model, Invoice invoice) {
		//TODO check if invoice already exist
		return null;
	}
	
	/**
	 * Check if an Account is valid.
	 * 
	 * @param account
	 * @return null if it's valid or a string explaining why is not valid.
	 */
	public static String isAccountChangeValid(Account account) {
		String error = "";

		if (account.getName() == null && account.getName().isBlank())
			error += "- La ragione sociale non può essere vuota\n";

		if (account.getFiscalCode() == null)
			error += "- Il codice fiscale non può essere nullo\n";
		else {
			String fiscaleCodeValidator = FiscalCodeValidator.validate(account.getFiscalCode());
			if (fiscaleCodeValidator != null)
				error += "- Il codice fiscale non è valido: " + fiscaleCodeValidator + "\n";
		}

		if ((account.getNumberVAT() == null || account.getNumberVAT().isEmpty()) && !account.getCategory().contains("b2c"))
			error += "- La partita IVA non può essere nulla\n";
		else {
			String vatNumberValidator = VATNumberValidator.validate(account.getNumberVAT());
			if (vatNumberValidator != null)
				error += "- La partita IVA non è valida: " + vatNumberValidator + "\n";
		}

		if (account.getCategory() == null)
			error += "- La categoria dell'account non può essere vuota\n";

		if ((account.getAtecoCode() != null && !account.getAtecoCode().isEmpty())
				&& !account.getAtecoCode().matches("\\d{2}[.]{1}\\d{2}[.]{1}[0-9A-Za-z]{1}"))
			error += "- Il codice ATECO inserito non è in un formato valido\n";

		// TODO find a way to validate address

		if (error.isEmpty())
			return null;
		else
			return error.trim().trim();
	}

	public static String isJobChangeValid(Job job) {
		String error = "";

		if (job.getId() == null || !job.getId().matches("((\\d{4}+)\\-(\\d{4}+))"))
			error += "- Il codice della pratica non è nel formato *ANNO-NUMERO_INCREMENTALE_DI_4_CIFRE*\n";

		if (job.getJobCategory() == null || job.getJobCategory().isEmpty() || job.getJobType() == null
				|| job.getJobType().isEmpty())
			error += "- La categoria e/o la sottocategoria non sono state inserite\n";

		if (error.isEmpty())
			return null;
		else
			return error.trim();
	}

	public static String isRSPPNoteChangeValid(String note) {
		// TODO add eventual field validation
		return null;
	}

	public static String isRSPPChangeValid(RSPP rspp) {
		String error = "";

		if(rspp.getStart() == null || rspp.getEnd() == null)
			error += "- La data di inzio incarico e/o la data di inizio incarico devono essere inserite\n";
		else {
			if (rspp.getStart().isAfter(rspp.getEnd()) || rspp.getStart().equals(rspp.getEnd()))
			error += "- La data di inizio dell'incarico deve essere precedente a quella di fine dell'incarico";
		}
		
		

		if (error.isEmpty())
			return null;
		else
			return error.trim();
	}

	public static String isInvoiceChangeValid(Invoice invoice) {
		String error = "";

		if (invoice.getNumber() <= 0)
			error += "- La fattura deve essere un numero maggiore di 0";

		if (error.isEmpty())
			return null;
		else
			return error.trim();
	}
	
	public static String isJobPAValid(JobPA job) {
		String error = "";
		
		if(job.getDecreeNumber() < 0)
			error += "- Il numero deve essere un numero maggiore di 0";
		
		if(!job.getCig().matches("^[a-zA-Z0-9]{10,}$"))
			error += "- Il CIG non è in un formato valido";
		if (error.isEmpty())
			return null;
		else
			return error.trim();
	}
}