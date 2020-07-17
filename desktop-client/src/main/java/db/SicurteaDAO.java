package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		String sql = "select *\n" + "from accounts.accounts a\n" + "where a.fiscalcode = ? ";
		Account account;

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, fiscalCode);

			ResultSet res = st.executeQuery();
			res.next();
			account = new Account(res.getString("fiscalcode"), res.getString("name"), res.getString("numbervat"),
					res.getString("atecocode"), res.getString("legal_address"), res.getString("customer_category"));
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
				"from jobs.jobs j where j.jobs_id = ? ) as j\n" + 
				"where j.customer = a.fiscalcode ";
		Account account;

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, job_id);

			ResultSet res = st.executeQuery();
			res.next();
			account = new Account(res.getString("fiscalcode"), res.getString("name"), res.getString("numbervat"),
					res.getString("atecocode"), res.getString("legal_address"), res.getString("customer_category"));
			conn.close();
			return account;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}
	
	
}