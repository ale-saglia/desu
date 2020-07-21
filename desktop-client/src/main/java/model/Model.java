package model;

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
}
