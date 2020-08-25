package dclient.db;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import org.jasypt.properties.EncryptableProperties;

import dclient.model.Account;
import dclient.model.Invoice;
import dclient.model.Job;
import dclient.model.JobPA;
import dclient.model.RSPP;

public class SicurteaDAO {
	int daysAdvance;
	
	EncryptableProperties config;
	Session session;

	public SicurteaDAO(EncryptableProperties config) {
		this.config = config;
		daysAdvance = Integer.parseInt(config.getProperty("rsppTable.daysAdvance", "14"));
		this.session = ConnectSSH.getSession(config);
	}

	public List<Map<String, String>> getDataForTable() {
		String sql = ("SELECT jobid,\n" + "       jobstart,\n" + "       jobend,\n" + "       \"name\",\n"
				+ "       category,\n" + "       n2.invoiceid,\n" + "       payed,\n" + "       notes\n"
				+ "FROM   (SELECT jobid,\n" + "               jobstart,\n" + "               jobend,\n"
				+ "               \"name\",\n" + "               category,\n" + "               invoiceid,\n"
				+ "               notes\n" + "        FROM   (SELECT r.rspp_jobid        AS jobid,\n"
				+ "                       r.jobstart          AS jobstart,\n"
				+ "                       r.jobstart          AS jobend,\n"
				+ "                       j.customer          AS fiscalcode,\n"
				+ "                       a.\"name\"            AS \"name\",\n"
				+ "                       a.customer_category AS category,\n"
				+ "                       r.invoiceid         AS invoiceid\n"
				+ "                FROM   accounts.accounts a,\n" + "                       deadlines.rspp r,\n"
				+ "                       jobs.jobs j\n" + "                WHERE  r.rspp_jobid = j.jobs_id\n"
				+ "                       AND j.customer = a.fiscalcode\n" + "                ORDER  BY \"name\",\n"
				+ "                          jobstart) AS n1\n" + "               LEFT JOIN deadlines.rspp_notes rn\n"
				+ "                      ON n1.fiscalcode = rn.fiscalcode) AS n2\n"
				+ "       LEFT JOIN invoices.invoices i\n" + "              ON i.invoiceid = n2.invoiceid ");
		List<Map<String, String>> tableElements;

		try {
			Connection conn = ConnectDB.getConnection(session, config);

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
			Connection conn = ConnectDB.getConnection(session, config);
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
			Connection conn = ConnectDB.getConnection(session, config);
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
			Connection conn = ConnectDB.getConnection(session, config);
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
			Connection conn = ConnectDB.getConnection(session, config);
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

			if (res.next() != false) {
				res.next();
				invoice = new Invoice(res.getString("invoiceid"), res.getInt("number"),
						res.getDate("emission").toLocalDate(), res.getString("type"), res.getBoolean("payed"));
				return invoice;
			}

			return null;

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
			Connection conn = ConnectDB.getConnection(session, config);
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
		this.session = ConnectSSH.getSession(config);
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

	public void updateAccount(String oldFiscalCode, Map<String, Object> data) {
		String query = "update accounts.accounts "
				+ "set fiscalcode = ? , name = ? , numbervat = ? , atecocode = ? , legal_address = ? , customer_category = ? "
				+ "where fiscalcode = ? ";

		try {
			Connection conn = ConnectDB.getConnection(session, config);
			PreparedStatement st = conn.prepareStatement(query);

			st.setString(1, (String) data.get("fiscalCode"));
			st.setString(2, (String) data.get("name"));
			st.setString(3, (String) data.get("numberVAT"));
			st.setString(4, (String) data.get("atecoCode"));
			st.setString(5, (String) data.get("legaAddress"));
			st.setString(6, (String) data.get("customerCategory"));
			st.setString(7, oldFiscalCode);

			st.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}

	public void updateJob(String oldJobCode, Map<String, Object> data) {
		String query = "update jobs.jobs "
				+ "set jobs_id = ? , jobs_category = ? , jobs_type = ? , jobs_description = ? " + "where jobs_id = ? ";

		try {
			Connection conn = ConnectDB.getConnection(session, config);
			PreparedStatement st = conn.prepareStatement(query);

			st.setString(1, (String) data.get("jobCode"));
			st.setString(2, (String) data.get("category"));
			st.setString(3, (String) data.get("type"));
			st.setString(4, (String) data.get("description"));
			st.setString(5, oldJobCode);

			st.executeQuery();

			if (data.containsKey("cig")) {
				query = "update jobs.jobs_pa \r\n"
						+ "set cig = ? , jobs_category = ? , decree_number = ? , decree_date = ? \r\n"
						+ "where job_id = ? ";

				st = conn.prepareStatement(query);

				st.setString(1, (String) data.get("cig"));
				st.setInt(2, Integer.parseInt((String) data.get("decreeNumber")));
				st.setDate(3, (Date.valueOf((LocalDate) data.get("decreeDate"))));
				st.setString(4, (String) data.get("jobCode"));

				st.executeQuery();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}

	}

	public void updateNote(String accountID, String note) {
		String query = "INSERT INTO deadlines.rspp_notes (fiscalcode, notes) " + "VALUES ( ? , ? ) "
				+ "ON CONFLICT (fiscalcode) DO UPDATE SET notes = ? where fiscalcode = ? ";

		try {
			Connection conn = ConnectDB.getConnection(session, config);
			PreparedStatement st = conn.prepareStatement(query);

			st.setString(1, accountID);
			st.setString(2, note);
			st.setString(3, note);
			st.setString(4, accountID);
			st.executeQuery();

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}

	public void updateRSPP(String jobCode, LocalDate oldJobStart, Map<String, Object> data) {
		String query = "update deadlines.rspp \r\n" + "set jobstart = ? , jobend = ? \r\n"
				+ "where rspp_jobid = ? and jobstart = ? ";

		try {
			Connection conn = ConnectDB.getConnection(session, config);
			PreparedStatement st = conn.prepareStatement(query);

			st.setDate(1, (Date.valueOf((LocalDate) data.get("jobStart"))));
			st.setDate(2, (Date.valueOf((LocalDate) data.get("jobEnd"))));
			st.setString(3, jobCode);
			st.setDate(4, (Date.valueOf(oldJobStart)));

			st.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}

	public void updateInvoice(String oldInvoiceID, String jobID, Map<String, Object> data) {
		String query = "INSERT INTO invoices.invoices (invoiceid, number, emission, type, payed) "
				+ "VALUES ( ? , ? , ? , ? , ? ) ON CONFLICT (invoiceid) DO UPDATE SET "
				+ "invoiceid = ? , number = ? , emission = ? , type = ? , payed = ? where invoiceid = ? ";

		try {
			Connection conn = ConnectDB.getConnection(session, config);
			PreparedStatement st = conn.prepareStatement(query);

			if (oldInvoiceID.equals(null) || oldInvoiceID.isBlank())
				st.setString(1, (String) data.get("invoiceID"));
			else
				st.setString(1, oldInvoiceID);
			st.setString(2, ((String) data.get("invoiceNumber")) + "/" + ((String) data.get("type")).toUpperCase() + " " + (LocalDate) data.get("invoiceEmissionDate"));
			st.setDate(3, (Date.valueOf((LocalDate) data.get("invoiceEmissionDate"))));
			st.setString(4, (String) data.get("type"));
			st.setBoolean(5, (Boolean) data.get("payed"));
			
			st.setString(7, (String) data.get("invoiceNumber"));
			st.setString(8, (String) data.get("invoiceNumber"));
			st.setDate(9, (Date.valueOf((LocalDate) data.get("invoiceEmissionDate"))));
			st.setString(10, (String) data.get("type"));
			st.setBoolean(11, (Boolean) data.get("payed"));
			st.setString(12, oldInvoiceID);
			
			st.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}
}