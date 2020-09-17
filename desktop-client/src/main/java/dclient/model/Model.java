package dclient.model;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import org.jasypt.properties.EncryptableProperties;

import com.google.common.collect.BiMap;

import dclient.Key;
import dclient.controllers.visualModels.RSPPtableElement;
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

	public List<RSPPtableElement> getDataForTable() {
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

	public void newInvoice(Invoice invoice) {
		dao.newInvoice(invoice);
	}
	
	public void updateInvoice(String oldInvoiceID, Invoice invoice) {
		dao.updateInvoice(oldInvoiceID, invoice);
	}
	
	public void matchRSPPInvoice(RSPP rspp, Invoice invoice) {
		dao.matchRSPPInvoice(rspp, invoice);
	}

	public static String getConfigPath() {
		return CONFIG_PATH;
	}

	public int newAccount(Account account) {
		return dao.newAccount(account);
	}

	public Account getAccount(String fiscalCode) {
		return dao.getAccount(fiscalCode);
	}

	public List<Account> getAllAccounts() {
		return dao.getAllAccounts();
	}
	
	public List<Job> getAllJobOfAccount(Account account){
		return dao.getAllJobOfAccount(account);
	}
	
	public int newJob(Job job) {
		return dao.newJob(job);
	}
	
	public int newRSPP(RSPP rspp) {
		return dao.newRSPP(rspp);		
	}
	
	public Account getAccountFromVATNumber(String vatNumber) {
		return dao.getAccountFromVATNumber(vatNumber);
	}
	
	public Collection<RSPP> getRSPPSet(Account account){
		return dao.getRSPPSet(account);
	}
	
	public Account getAccountOfInvoice(Invoice invoice) {
		return dao.getAccountOfInvoice(invoice);
	}
	
	public RSPP getLastRSPP(Account account){
		return dao.getLastRSPP(account);
	}
	
	public int addJobPAInfos(JobPA jobPA) {
		return dao.addJobPAInfos(jobPA);
	}
}