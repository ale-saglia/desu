package dclient.model;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.jasypt.properties.EncryptableProperties;

import dclient.Key;
import dclient.db.SicurteaDAO;

public class Model {
	EncryptableProperties config;
	SicurteaDAO dao;

	Map<String, String> accountCategories;

	List<String> jobCategories;
	List<String> jobTypes;

	public Model(String userPassword) throws IOException {
		config = new EncryptableProperties((new Key(userPassword)).getEnc());
		config.load(Key.class.getResourceAsStream("config.properties"));

		this.dao = new SicurteaDAO(config);

		accountCategories = dao.getAccountsCategories();
		jobCategories = dao.getJobCategories();
		jobTypes = dao.getJobTypes();
	}

	public RSPP getRSPP(String jobID, LocalDate jobStart) {
		return dao.getRSPP(jobID, jobStart);
	}

	public Map<String, String> getAccountCategories() {
		return accountCategories;
	}

	public List<String> getJobCategories() {
		return jobCategories;
	}

	public List<String> getJobTypes() {
		return jobTypes;
	}

	public String getRSPPnote(String fiscalCode) {
		return dao.getRSPPnote(fiscalCode);
	}

	public List<Map<String, String>> getDataForTable() {
		return dao.getDataForTable();
	}

	public void refreshSession() {
		if (!dao.getSession().isConnected())
			dao.getNewSession();
	}

	public String closeSession() {
		return dao.closeSession();
	}

	public EncryptableProperties getConfig() {
		return config;
	}

	public void updateAccount(String oldFiscalCode, Map<String, Object> data) {
		dao.updateAccount(oldFiscalCode, data);
	}

	public void updateJob(String oldJobCode, Map<String, Object> data) {
		dao.updateJob(oldJobCode, data);
	}

	public void updateNote(String accountID, String note) {
		dao.updateNote(accountID, note);
	}

	public void updateRSPP(String jobCode, LocalDate oldJobStart, Map<String, Object> data) {
		dao.updateRSPP(jobCode, oldJobStart, data);
	}

	public void updateInvoice(String oldInvoiceID, String jobID, Map<String, Object> data) {
		dao.updateInvoice(oldInvoiceID, jobID, data);
	}
}