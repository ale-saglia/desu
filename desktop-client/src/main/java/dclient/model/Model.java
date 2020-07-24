package dclient.model;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.jasypt.properties.EncryptableProperties;

import dclient.App;
import dclient.Key;
import dclient.db.SicurteaDAO;

public class Model {
	EncryptableProperties config;
	SicurteaDAO dao;

	Map<String, String> accountCategories;

	List<String> jobCategories;
	List<String> jobTypes;

	public Model() throws IOException {
		config = new EncryptableProperties((new Key("")).getEnc());
		config.load(App.class.getResourceAsStream("config.properties"));

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
}
