package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import model.Account;
import model.Job;

public class SicurteaDAO {
	final int DEADLINES_DAYS_ADVANCE = 14;

	public List<Map<String, String>> getDataForTable(boolean deadlineOn) {
		String sql = Queries.getRSPPtable();
		List<Map<String, String>> tableElements;

		try {
			Connection conn = ConnectDB.getConnection();

			tableElements = new ArrayList<Map<String, String>>();
			Map<String, String> temp;

			PreparedStatement st = conn.prepareStatement(sql);

			ResultSet res = st.executeQuery();

			while (res.next()) {
				temp = new HashMap<String, String>();
				temp.put("name", res.getString("name"));
				temp.put("category", res.getString("category"));
				temp.put("jobend", (new SimpleDateFormat("dd/MM/yyyy")).format(res.getDate("jobend")));
				temp.put("invoiceid", res.getString("invoiceid"));
				temp.put("payed", String.valueOf(res.getBoolean("payed")));

				// Useful for tracking selected field when view / edit
				temp.put("jobid", res.getString("jobid"));
				temp.put("jobstart", (new SimpleDateFormat("dd/MM/yyyy")).format(res.getDate("jobstart")));
				tableElements.add(temp);
			}

			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}

		return tableElements;
	}

	public Job getJob(String job_id) {
		String sql = "select * from jobs.jobs j where j.jobs_id = ? ";
		Job job;

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, job_id);
			System.out.println("Pollo ->" + st);			

			ResultSet res = st.executeQuery();
			res.next();
			job = new Job(res.getString("jobs_id"), res.getString("jobs_category"), res.getString("jobs_type"),
					res.getString("jobs_description"));
			conn.close();
			return job;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}
	
	public Account getAccount(String fiscalCode) {
		String sql = "select * from accounts.accounts a, accounts.accounts_categories ac where a.fiscalcode = ?  and  a.customer_category = ac.categories";
		Account account;

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, fiscalCode);

			ResultSet res = st.executeQuery();
			res.next();
			account = new Account(res.getString("fiscalcode"), res.getString("name"), res.getString("numbervat"),
					res.getString("atecocode"), res.getString("legal_address"), res.getString("extended"));
			conn.close();
			return account;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}
	
	public Account getAccountFromJob(String job_id) {
		String sql = "select *\n" + 
				"from accounts.accounts a, (select j.customer\n" + 
				"from jobs.jobs j where j.jobs_id = ? ) j,\n" + 
				"accounts.accounts_categories ac\n" + 
				"where j.customer = a.fiscalcode\n" + 
				"and  a.customer_category = ac.categories ";
		Account account;

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, job_id);

			ResultSet res = st.executeQuery();
			res.next();
			account = new Account(res.getString("fiscalcode"), res.getString("name"), res.getString("numbervat"),
					res.getString("atecocode"), res.getString("legal_address"), res.getString("extended"));
			conn.close();
			return account;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}
	
	public Map<String, String> getAccountsCategories(){
		String sql = "select * from accounts.accounts_categories ac ";
		Map<String, String> categories = new TreeMap<String, String>();
		
		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);

			ResultSet res = st.executeQuery();
			while (res.next()) {
				categories.put(res.getString("categories"), res.getString("extended"));
			}
			
			return categories;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}	
	
	public List<String> getJobCategories(){
		String sql = "select * from jobs.jobs_categories jc";
		List<String> categories = new LinkedList<String>();
		
		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);

			ResultSet res = st.executeQuery();
			while (res.next()) {
				categories.add(res.getString("categories"));
			}
			
			return categories;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}	
	
	public List<String> getJobTypes(){
		String sql = "select * from jobs.job_types jt ";
		List<String> types = new LinkedList<String>();
		
		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);

			ResultSet res = st.executeQuery();
			while (res.next()) {
				types.add(res.getString("types"));
			}
			
			return types;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}	
}