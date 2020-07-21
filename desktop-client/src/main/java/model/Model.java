package model;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import db.SicurteaDAO;

public class Model {
	SicurteaDAO dao;

	Map<String, String> accountCategories;

	List<String> jobCategories;
	List<String> jobTypes;

	public Model() {
		this.dao = new SicurteaDAO();

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

	public void closeSession() {
		dao.closeSession();
	}
}
