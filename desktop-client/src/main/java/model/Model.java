package model;

import java.util.List;
import java.util.Map;

import db.SicurteaDAO;

public class Model {
	SicurteaDAO dao;
	
	public Model() {
		this.dao = new SicurteaDAO();
	}
}
