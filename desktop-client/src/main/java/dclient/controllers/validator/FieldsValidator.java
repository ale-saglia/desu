package dclient.controllers.validator;

import java.time.format.DateTimeFormatter;

import dclient.model.*;

public class FieldsValidator {

	public static String isNewAccountDuplicate(Model model, Account account) {
		String error = "";

		Account foundAccount = model.getAccount(account.getFiscalCode());
		if (foundAccount != null)
			error += "- Il codice fiscale è già presente nel database per " + foundAccount.getName() + "\n";

		foundAccount = model.getAccountFromVATNumber(account.getNumberVAT());
		if (foundAccount != null)
			error += "- La partita IVA è già presente nel database per " + foundAccount.getName() + "\n";

		if (error.isEmpty())
			return null;
		else
			return error.trim();
	}

	public static String isNewInvoiceDuplicate(Model model, Invoice invoice) {
		String error = "";

		Account account = model.getAccount(invoice);
		if (account != null)
			error += "- La fattura è già presente nel database per " + account.getName();

		RSPP rspp = model.getRSPPfromInvoice(invoice);
		if (rspp != null)
			error += "- La fattura inserita è gia presente per la pratica " + rspp.getJob().getId()
					+ " con l'incarico tra il "
					+ rspp.getStart().format(DateTimeFormatter.ofPattern(model.getConfig().getProperty("dateFormat")))
					+ " e il " + rspp.getEnd().format(DateTimeFormatter.ofPattern(model.getConfig().getProperty("dateFormat")));

		if (error.isEmpty())
			return null;
		else
			return error.trim();
	}

	/**
	 * Check if an Account is valid.
	 * 
	 * @param account
	 * @return null if it's valid or a string explaining why is not valid.
	 */
	public static String isAccountValid(Account account) {
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

		if ((account.getNumberVAT() == null || account.getNumberVAT().isEmpty())
				&& !account.getCategory().contains("b2c"))
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

	public static String isJobValid(Job job) {
		String error = "";

		if (job.getId() == null || !job.getId().matches("((\\d{4}+)\\-(\\d{4}+))"))
			error += "- Il codice della pratica non è nel formato *ANNO-NUMERO_INCREMENTALE_DI_4_CIFRE*\n";

		if (job.getJobCategory() == null || job.getJobCategory().isEmpty() || job.getJobType() == null
				|| job.getJobType().isEmpty())
			error += "- La categoria e/o la sottocategoria non sono state inserite\n";

		if (job instanceof JobPA) {
			String paError = isJobPAValid((JobPA) job);
			if (paError != null)
				error += paError;
		}

		if (error.isEmpty())
			return null;
		else
			return error.trim();
	}

	public static String isRSPPNoteValid(String note) {
		// TODO add eventual field validation
		return null;
	}

	public static String isRSPPChangeValid(RSPP rspp) {
		String error = "";

		if (rspp.getStart() == null || rspp.getEnd() == null)
			error += "- La data di inzio incarico e/o la data di inizio incarico devono essere inserite\n";
		else {
			if (rspp.getStart().isAfter(rspp.getEnd()) || rspp.getStart().equals(rspp.getEnd()))
				error += "- La data di inizio dell'incarico deve essere precedente a quella di fine dell'incarico\n";
		}

		if (error.isEmpty())
			return null;
		else
			return error.trim();
	}

	public static String isInvoiceValid(Invoice invoice) {
		String error = "";

		if (invoice.getNumber() != null) {
			if (invoice.getNumber() <= 0)
				error += "- La fattura deve essere un numero maggiore di 0\n";
		} else
			error += "- La fattura non è un numero valido\n";

		if (error.isEmpty())
			return null;
		else
			return error.trim();
	}

	private static String isJobPAValid(JobPA job) {
		String error = "";

		if (job.getDecreeNumber() != null) {
			if (job.getDecreeNumber() < 0)
				error += "- Il numero deve essere un numero maggiore di 0\n";
		} else
			error += "- Il numero di determina non è in un formato valido\n";

		if (!job.getCig().matches("^[a-zA-Z0-9]{10,}$"))
			error += "- Il CIG non è in un formato valido\n";
		if (error.isEmpty())
			return null;
		else
			return error.trim();
	}
}