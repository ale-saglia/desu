package dclient.controllers.validator;

import java.time.format.DateTimeFormatter;

import dclient.db.dao.AccountDAO;
import dclient.db.dao.RsppDAO;
import dclient.model.*;

public class FieldsValidator {

	public static String isNewAccountDuplicate(Model model, Account account) {
		String error = "";

		Account foundAccount = AccountDAO.getAccount(model.getConMan().getDBConnection(), account.getFiscalCode());
		if (foundAccount != null)
			error += "- Il codice fiscale è già presente nel database per " + foundAccount.getName() + "\n";

		foundAccount = AccountDAO.getAccountFromVATNumber(model.getConMan().getDBConnection(), account.getNumberVAT());
		if (foundAccount != null)
			error += "- La partita IVA è già presente nel database per " + foundAccount.getName() + "\n";

		return returnManager(error);
	}

	public static String isNewInvoiceDuplicate(Model model, Invoice invoice, Rspp rspp) {
		String error = "";

		Account account = AccountDAO.getAccount(model.getConMan().getDBConnection(), invoice);
		if (account != null)
			error += "- La fattura è già presente nel database per " + account.getName();

		Rspp fetchedRSPP = RsppDAO.getRSPP(model.getConMan().getDBConnection(), invoice);
		if (fetchedRSPP != null && !rspp.equals(fetchedRSPP))
			error += "- La fattura inserita è gia presente per la pratica " + fetchedRSPP.getJob().getId()
					+ " con l'incarico tra il "
					+ fetchedRSPP.getStart()
							.format(DateTimeFormatter.ofPattern(model.getConfig().getProperty("dateFormat")))
					+ " e il " + fetchedRSPP.getEnd()
							.format(DateTimeFormatter.ofPattern(model.getConfig().getProperty("dateFormat")));

		return returnManager(error);
	}

	/**
	 * Check if an Account is valid.
	 * 
	 * @param account
	 * @return null if it's valid or a string explaining why is not valid.
	 */
	public static String isAccountValid(Account account) {
		String error = "";

		if (account.getName() == null)
			error += "- La ragione sociale non può essere vuota\n";

		if (account.getFiscalCode() == null)
			error += "- Il codice fiscale non può essere nullo\n";
		else if (FiscalCodeValidator.validate(account.getFiscalCode()) != null)
			error += "- Il codice fiscale non è valido: " + FiscalCodeValidator.validate(account.getFiscalCode())
					+ "\n";

		if ((account.getNumberVAT() == null || account.getNumberVAT().isEmpty())
				&& !account.getCategory().contains("b2c"))
			error += "- La partita IVA non può essere nulla\n";
		else if (VATNumberValidator.validate(account.getNumberVAT()) != null)
			error += "- La partita IVA non è valida: " + VATNumberValidator.validate(account.getNumberVAT()) + "\n";

		if (account.getCategory() == null)
			error += "- La categoria dell'account non può essere vuota\n";

		if ((account.getAtecoCode() != null && !account.getAtecoCode().isEmpty())
				&& !account.getAtecoCode().matches("\\d{2}[.]{1}\\d{2}[.]{1}[0-9A-Za-z]{1}"))
			error += "- Il codice ATECO inserito non è in un formato valido\n";

		// TODO find a way to validate address

		return returnManager(error);
	}

	public static String isJobValid(Job job) {
		String error = "";

		if (job.getId() == null || !job.getId().matches("((\\d{4}+)\\-(\\d{4}+))"))
			error += "- Il codice della pratica non è nel formato *ANNO-NUMERO_INCREMENTALE_DI_4_CIFRE*\n";

		if (job.getJobCategory() == null || job.getJobCategory().isEmpty() || job.getJobType() == null
				|| job.getJobType().isEmpty())
			error += "- La categoria e/o la sottocategoria non sono state inserite\n";

		if (job instanceof JobPA && isJobPAValid((JobPA) job) != null)
			error += isJobPAValid((JobPA) job);

		return returnManager(error);
	}

	public static String isRSPPNoteValid(String note) {
		// TODO add eventual field validation
		return null;
	}

	public static String isRSPPChangeValid(Rspp rspp) {
		String error = "";

		if (rspp.getStart() == null || rspp.getEnd() == null)
			error += "- La data di inzio incarico e/o la data di inizio incarico devono essere inserite in un formato valido\n";
		else if (rspp.getStart().isAfter(rspp.getEnd()) || rspp.getStart().equals(rspp.getEnd()))
			error += "- La data di inizio dell'incarico deve essere precedente a quella di fine dell'incarico\n";

		return returnManager(error);
	}

	public static String isInvoiceValid(Invoice invoice) {
		String error = "";

		if (invoice.getNumber() == null)
			error += "- La fattura non è un numero valido\n";
		else if (invoice.getNumber() <= 0)
			error += "- La fattura deve essere un numero maggiore di 0\n";

		return returnManager(error);
	}

	private static String isJobPAValid(JobPA job) {
		String error = "";

		if (job.getDecreeNumber() == null)
			error += "- Il numero di determina non è in un formato valido\n";
		else if (job.getDecreeNumber() < 0)
			error += "- Il numero deve essere un numero maggiore di 0\n";

		if (!job.getCig().matches("^[a-zA-Z0-9]{10,}$"))
			error += "- Il CIG non è in un formato valido\n";

		if (job.getCig() == null)
			error += "- La data del decreto è nulla o in un formato non valido\n";

		return returnManager(error);
	}

	private static String returnManager(String error) {
		if (error.isEmpty())
			return null;
		else
			return error.trim();
	}
}