package dclient.db;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import dclient.model.Account;
import dclient.model.Invoice;
import dclient.model.Job;
import dclient.model.JobPA;
import dclient.model.RSPP;

public class SicurteaDAO {
	final int DEADLINES_DAYS_ADVANCE = 14;
	Session session;

	public SicurteaDAO() {
		this.session = ConnectDB.getSession();
	}

	public List<Map<String, String>> getDataForTable() {
		String sql = (
					"SELECT jobid,\n" + 
					"       jobstart,\n" + 
					"       jobend,\n" + 
					"       \"name\",\n" + 
					"       category,\n" + 
					"       n2.invoiceid,\n" + 
					"       payed,\n" + 
					"       notes\n" + 
					"FROM   (SELECT jobid,\n" + 
					"               jobstart,\n" + 
					"               jobend,\n" + 
					"               \"name\",\n" + 
					"               category,\n" + 
					"               invoiceid,\n" + 
					"               notes\n" + 
					"        FROM   (SELECT r.rspp_jobid        AS jobid,\n" + 
					"                       r.jobstart          AS jobstart,\n" + 
					"                       r.jobstart          AS jobend,\n" + 
					"                       j.customer          AS fiscalcode,\n" + 
					"                       a.\"name\"            AS \"name\",\n" + 
					"                       a.customer_category AS category,\n" + 
					"                       r.invoiceid         AS invoiceid\n" + 
					"                FROM   accounts.accounts a,\n" + 
					"                       deadlines.rspp r,\n" + 
					"                       jobs.jobs j\n" + 
					"                WHERE  r.rspp_jobid = j.jobs_id\n" + 
					"                       AND j.customer = a.fiscalcode\n" + 
					"                ORDER  BY \"name\",\n" + 
					"                          jobstart) AS n1\n" + 
					"               LEFT JOIN deadlines.rspp_notes rn\n" + 
					"                      ON n1.fiscalcode = rn.fiscalcode) AS n2\n" + 
					"       LEFT JOIN invoices.invoices i\n" + 
					"              ON i.invoiceid = n2.invoiceid "
				);
		List<Map<String, String>> tableElements;

		try {
			Connection conn = ConnectDB.getConnection(session);

			tableElements = new ArrayList<Map<String, String>>();
			Map<String, String> temp;

			PreparedStatement st = conn.prepareStatement(sql);

			ResultSet res = st.executeQuery();

			while (res.next()) {
				temp = new HashMap<String, String>();
				temp.put("name", res.getString("name"));
				temp.put("category", res.getString("category"));
				temp.put("jobend", res.getDate("jobend").toString());
				temp.put("invoiceid", res.getString("invoiceid"));
				temp.put("payed", String.valueOf(res.getBoolean("payed")));
				temp.put("note", res.getString("notes"));

				// Useful for tracking selected field when view / edit
				temp.put("jobid", res.getString("jobid"));
				temp.put("jobstart", res.getDate("jobstart").toString());
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

	public Map<String, String> getAccountsCategories() {
		String sql = "select * from accounts.accounts_categories ac ";
		Map<String, String> categories = new TreeMap<String, String>();

		try {
			Connection conn = ConnectDB.getConnection(session);
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

	public List<String> getJobCategories() {
		String sql = "select * from jobs.jobs_categories jc";
		List<String> categories = new LinkedList<String>();

		try {
			Connection conn = ConnectDB.getConnection(session);
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

	public List<String> getJobTypes() {
		String sql = "select * from jobs.job_types jt ";
		List<String> types = new LinkedList<String>();

		try {
			Connection conn = ConnectDB.getConnection(session);
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

	public RSPP getRSPP(String jobID, LocalDate startJob) {
		String sql = "select * from deadlines.rspp r where r.rspp_jobid = ? and r.jobstart = ? ";
		RSPP rspp;

		try {
			Connection conn = ConnectDB.getConnection(session);
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, jobID);
			st.setDate(2, Date.valueOf(startJob));

			ResultSet res = st.executeQuery();
			res.next();
			rspp = new RSPP(getJob(res.getString("rspp_jobid"), conn), res.getDate("jobstart").toLocalDate(),
					res.getDate("jobend").toLocalDate(), getInvoice(res.getString("invoiceid"), conn));
			conn.close();
			return rspp;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}

	private Job getJob(String job_id, Connection conn) {
		String sql = "select * from jobs.jobs j where j.jobs_id = ? ";
		Job job;

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, job_id);

			ResultSet res = st.executeQuery();
			res.next();
			job = new Job(res.getString("jobs_id"), res.getString("jobs_category"), res.getString("jobs_type"),
					res.getString("jobs_description"), getAccount(res.getString("customer"), conn));

			if (job.getCustomer().getCategory().contains("pa")) {
				sql = "select * from jobs.jobs_pa where job_id = ? ";
				st = conn.prepareStatement(sql);
				st.setString(1, job.getId());
				res = st.executeQuery();
				res.next();
				job = new JobPA(job, res.getString("cig"), res.getInt("decree_number"),
						res.getDate("decree_date").toLocalDate());
			}

			return job;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}

	private Invoice getInvoice(String invoiceID, Connection conn) {
		String sql = "select * from invoices.invoices i where invoiceid = ? ";
		Invoice invoice;

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, invoiceID);

			ResultSet res = st.executeQuery();
			res.next();
			invoice = new Invoice(res.getString("invoiceid"), res.getInt("number"),
					res.getDate("emission").toLocalDate(), res.getString("type"), res.getBoolean("payed"));
			return invoice;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}

	private Account getAccount(String fiscalCode, Connection conn) {
		String sql = "select * from accounts.accounts a where a.fiscalcode = ? ";
		Account account;

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, fiscalCode);

			ResultSet res = st.executeQuery();
			res.next();
			account = new Account(res.getString("fiscalcode"), res.getString("name"), res.getString("numbervat"),
					res.getString("atecocode"), res.getString("legal_address"), res.getString("customer_category"));
			return account;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}

	public String getRSPPnote(String fiscalCode) {
		String sql = "select * from deadlines.rspp_notes r where r.fiscalcode = ? ";
		String notes;

		try {
			Connection conn = ConnectDB.getConnection(session);
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, fiscalCode);

			ResultSet res = st.executeQuery();
			if (res.next() == false)
				return "";
			else {
				notes = res.getString("notes");
				conn.close();
				return notes;
			}

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}

	public Session getSession() {
		return session;
	}

	public void getNewSession() {
		this.session = ConnectDB.getSession();
	}

	public String closeSession() {
		String message = null;
		try {
			message = "Closing: " + session.getPortForwardingL();
		} catch (JSchException e) {
			e.printStackTrace();
		}
		this.session.disconnect();
		return message;
	}
}