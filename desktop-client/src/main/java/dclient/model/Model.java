package dclient.model;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.jasypt.properties.EncryptableProperties;

import com.google.common.collect.BiMap;

import dclient.Key;
import dclient.db.SicurteaDAO;

public class Model {
	InputStream cfg;
	public final static String CONFIG_PATH = System.getProperty("user.home") + "/.dclient/";
	Properties config;
	
	SicurteaDAO dao;

	BiMap<String, String> accountCategories;

	List<String> jobCategories;
	List<String> jobTypes;

	public Model(String userPassword) throws IOException {
		config = new Properties();
		config.load(new FileInputStream(CONFIG_PATH + "config.properties"));	
		config = new EncryptableProperties((new Key(userPassword, config)).getEnc());
		config.load(new FileInputStream(CONFIG_PATH + "config.properties"));

		this.dao = new SicurteaDAO(config);

		accountCategories = dao.getAccountsCategories();
		jobCategories = dao.getJobCategories();
		jobTypes = dao.getJobTypes();
	}

	public RSPP getRSPP(String jobID, LocalDate jobStart) {
		return dao.getRSPP(jobID, jobStart);
	}

	public BiMap<String, String> getAccountCategories() {
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

	public Properties getConfig() {
		return config;
	}

	public void updateAccount(String oldFiscalCode, Account account) {
		dao.updateAccount(oldFiscalCode, account);
	}

	public void updateJob(String oldJobCode, Job job) {
		dao.updateJob(oldJobCode, job);
	}

	public void updateNote(String accountID, String note) {
		dao.updateNote(accountID, note);
	}

	public void updateRSPP(String jobCode, LocalDate oldJobStart, RSPP rspp) {
		dao.updateRSPP(jobCode, oldJobStart, rspp);
	}

	public void updateInvoice(String oldInvoiceID, Invoice invoice, RSPP rspp) {
		dao.updateInvoice(oldInvoiceID, invoice, rspp);
	}
	
	public static String getConfigPath() {
		return CONFIG_PATH;
	}
}