package model;

import db.SicurteaDAO;

public class Model {
	SicurteaDAO dao;
	
	public Model() {
		this.dao = new SicurteaDAO();
	}
}
