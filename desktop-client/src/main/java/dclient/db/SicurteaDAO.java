package dclient.db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.base.Charsets;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.io.Resources;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import dclient.controllers.visualModels.RSPPtableElement;
import dclient.model.Account;
import dclient.model.Invoice;
import dclient.model.Job;
import dclient.model.JobPA;
import dclient.model.RSPP;

public class SicurteaDAO {
	int daysAdvance;

	Properties config;
	Session session;

	public SicurteaDAO(Properties config) {
		this.config = config;
		daysAdvance = Integer.parseInt(config.getProperty("rsppTable.daysAdvance", "14"));
		this.session = ConnectSSH.getSession(config);
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

	private List<Account> resToAccounts(ResultSet res){
		List<Account> accountSet = new ArrayList<Account>();
		try {
			while (res.next()) {
				accountSet.add(new Account(res.getString("fiscalcode"), res.getString("name"), res.getString("numbervat"),
						res.getString("atecocode"), res.getString("legal_address"), res.getString("customer_category"),
						res.getString("descriptor")));
			}
			return accountSet;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	private Set<Invoice> resToInvoice(ResultSet res) {
		Set<Invoice> invoiceList = new TreeSet<Invoice>();
		try {
			while (res.next()) {
				invoiceList.add(new Invoice(res.getString("invoiceid"), res.getInt("number"),
						res.getDate("emission").toLocalDate(), res.getString("type"), res.getBoolean("payed")));
			}
			return invoiceList;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public BiMap<String, String> getAccountsCategories() {
		String sql = "select * from accounts.accounts_categories ac ";
		BiMap<String, String> categories = HashBiMap.create();

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

	public List<RSPPtableElement> getDataForTable() {
		String sql;

		try {
			sql = Resources.toString(this.getClass().getResource("mainViewQuery.sql"), Charsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		List<RSPPtableElement> tableElements = new LinkedList<RSPPtableElement>();

		try {
			Connection conn = ConnectDB.getConnection(session, config);
			PreparedStatement st = conn.prepareStatement(sql);

			ResultSet res = st.executeQuery();

			while (res.next())
				tableElements.add(new RSPPtableElement(res.getString("name"), res.getString("descriptor"),
						res.getString("category"), res.getDate("jobend").toLocalDate(), res.getString("invoices"),
						res.getBoolean("payed"), res.getString("notes"), res.getString("jobid"),
						res.getDate("jobstart").toLocalDate()));

			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}

		return tableElements;
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
					res.getDate("jobend").toLocalDate());
			rspp.setInvoice(getInvoiceRSPP(rspp));
			conn.close();
			return rspp;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}

	public RSPP getLastRSPP(Account account) {
		String sql = "select * from deadlines.rspp r, jobs.jobs j where r.rspp_jobid = j.jobs_id and customer = ? order by jobend desc LIMIT 1 ";
		RSPP rspp = null;

		try {
			Connection conn = ConnectDB.getConnection(session, config);
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, account.getFiscalCode());

			ResultSet res = st.executeQuery();
			if (res.next() == false)
				return null;
			rspp = new RSPP(getJob(res.getString("rspp_jobid"), conn), res.getDate("jobstart").toLocalDate(),
					res.getDate("jobend").toLocalDate());
			rspp.setInvoice(getInvoiceRSPP(rspp));
			conn.close();
			return rspp;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}

	public Collection<RSPP> getRSPPList() {
		String sql = "select * from deadlines.rspp";
		List<RSPP> rsppList = new LinkedList<RSPP>();

		try {
			Connection conn = ConnectDB.getConnection(session, config);
			PreparedStatement st = conn.prepareStatement(sql);

			ResultSet res = st.executeQuery();

			while (res.next()) {
				RSPP rspp = new RSPP(getJob(res.getString("rspp_jobid"), conn), res.getDate("jobstart").toLocalDate(),
						res.getDate("jobend").toLocalDate());
				rspp.setInvoice(getInvoiceRSPP(rspp));
				rsppList.add(rspp);
			}
			conn.close();
			return rsppList;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}

	public Collection<RSPP> getRSPPSet(Account account) {
		String sql = "select * from deadlines.rspp r, jobs.jobs j where r.rspp_jobid = j.jobs_id and customer = ? ";
		List<RSPP> rsppList = new LinkedList<RSPP>();

		try {
			Connection conn = ConnectDB.getConnection(session, config);
			PreparedStatement st = conn.prepareStatement(sql);

			st.setString(1, account.getFiscalCode());

			ResultSet res = st.executeQuery();

			while (res.next()) {
				RSPP rspp = new RSPP(getJob(res.getString("rspp_jobid"), conn), res.getDate("jobstart").toLocalDate(),
						res.getDate("jobend").toLocalDate());
				rspp.setInvoice(getInvoiceRSPP(rspp));
				rsppList.add(rspp);
			}
			conn.close();
			return rsppList;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}

	public Collection<Invoice> getInvoiceRSPP(RSPP rspp) {
		String query = "select i.* from( select invoice_id from deadlines.rspp_invoices ri where rspp_id = ? and rspp_start = ?) as vi, invoices.invoices i where vi.invoice_id = i.invoiceid";
		Set<Invoice> invoicesMap = new TreeSet<Invoice>();

		try {
			Connection conn = ConnectDB.getConnection(session, config);
			PreparedStatement st = conn.prepareStatement(query);

			st.setString(1, rspp.getJob().getId());
			st.setDate(2, Date.valueOf(rspp.getStart()));

			invoicesMap = resToInvoice(st.executeQuery());

			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}

		return invoicesMap;
	}

	public Collection<Invoice> getInvoiceRSPP(String rspp_id, LocalDate rspp_start) {
		String query = "select i.* from( select invoice_id from deadlines.rspp_invoices ri where rspp_id = ? and rspp_start = ?) as vi, invoices.invoices i where vi.invoice_id = i.invoiceid";
		Set<Invoice> invoicesMap = new TreeSet<Invoice>();

		try {
			Connection conn = ConnectDB.getConnection(session, config);
			PreparedStatement st = conn.prepareStatement(query);

			st.setString(1, rspp_id);
			st.setDate(2, Date.valueOf(rspp_start));

			invoicesMap = resToInvoice(st.executeQuery());

			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}

		return invoicesMap;
	}

	public int addJobPAInfos(JobPA jobPA) {
		String query = "insert into jobs.jobs_pa (jobs_id, cig, decree_number, decree_date) values( ? , ? , ? , ? ) "
				+ "on conflict (jobs_id) do update set cig = ? , decree_number= ? , decree_date= ? ";

		int rowsAffected = 0;

		try {
			Connection conn = ConnectDB.getConnection(session, config);
			PreparedStatement st = conn.prepareStatement(query);

			st.setString(1, jobPA.getId());
			st.setString(2, jobPA.getCig());
			st.setInt(3, jobPA.getDecreeNumber());
			st.setDate(4, Date.valueOf(jobPA.getDecreeDate()));

			st.setString(5, jobPA.getCig());
			st.setInt(6, jobPA.getDecreeNumber());
			st.setDate(7, Date.valueOf(jobPA.getDecreeDate()));

			rowsAffected = st.executeUpdate();

			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}

		System.out.println("Rows updated JOBS_PA => " + rowsAffected);
		return rowsAffected;
	}

	private Job getJob(String job_id, Connection conn) {
		String sql = "select * from jobs.jobs j where j.jobs_id = ? ";
		Job job;

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, job_id);

			ResultSet res = st.executeQuery();
			if (res.next() == false)
				return null;
			job = new Job(res.getString("jobs_id"), res.getString("jobs_category"), res.getString("jobs_type"),
					res.getString("jobs_description"), getAccount(res.getString("customer"), conn));

			if (job.getCustomer().getCategory().contains("pa")) {
				sql = "select * from jobs.jobs_pa where jobs_id = ? ";
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

	private Account getAccount(String fiscalCode, Connection conn) {
		String sql = "select * from accounts.accounts a where a.fiscalcode = ? ";
		Account account;

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, fiscalCode);

			ResultSet res = st.executeQuery();

			account = resToAccounts(res).get(0);
			return account;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}

	public Account getAccount(String fiscalCode) {
		String sql = "select * from accounts.accounts a where a.fiscalcode = ? ";
		Account account = null;

		try {
			Connection conn = ConnectDB.getConnection(session, config);
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, fiscalCode);

			ResultSet res = st.executeQuery();

			account = resToAccounts(res).get(0);
			
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
			conn.close();
			if (res.next() == false)
				return "";
			else {
				notes = res.getString("notes");
				return notes;
			}

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}

	public void updateAccount(String oldFiscalCode, Account account) {
		String query = "UPDATE accounts.accounts "
				+ "SET fiscalcode = ?, \"name\" = ?, numbervat = ?, atecocode = ?, legal_address = ?, customer_category = ? , descriptor = ? "
				+ "WHERE fiscalcode = ? ";

		int rowsAffected = 0;

		try {
			Connection conn = ConnectDB.getConnection(session, config);
			PreparedStatement st = conn.prepareStatement(query);

			st.setString(1, account.getFiscalCode());
			st.setString(2, account.getName());
			st.setString(3, account.getNumberVAT());

			if (account.getAtecoCode() == null || account.getAtecoCode().isEmpty())
				st.setNull(4, Types.VARCHAR);
			else
				st.setString(4, account.getAtecoCode());

			if (account.getLegalAddress() == null || account.getLegalAddress().isEmpty())
				st.setNull(5, Types.VARCHAR);
			else
				st.setString(5, account.getLegalAddress());

			st.setString(6, account.getCategory());

			if (account.getDescriptor() == null || account.getDescriptor().isEmpty())
				st.setNull(7, Types.VARCHAR);
			else
				st.setString(7, account.getDescriptor());

			st.setString(8, oldFiscalCode);

			rowsAffected = st.executeUpdate();

			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}

		System.out.println("Rows updated ACCOUNT => " + rowsAffected);
	}

	public void updateJob(String oldJobCode, Job job) {
		String query = "update jobs.jobs " + "set jobs_id = ?, jobs_category = ?, jobs_type = ?, jobs_description = ? "
				+ "where jobs_id = ? ";

		int rowsAffected = 0;

		try {
			Connection conn = ConnectDB.getConnection(session, config);
			PreparedStatement st = conn.prepareStatement(query);

			st.setString(1, job.getId());
			st.setString(2, job.getJobCategory());
			st.setString(3, job.getJobType());
			st.setString(4, job.getDescription());
			st.setString(5, oldJobCode);

			rowsAffected = st.executeUpdate();

			if (job instanceof JobPA) {
				query = "update jobs.jobs_pa " + "set cig = ? , decree_number = ? , decree_date = ? "
						+ "where jobs_id = ? ";

				st = conn.prepareStatement(query);

				st.setString(1, ((JobPA) job).getCig());
				st.setInt(2, ((JobPA) job).getDecreeNumber());
				st.setDate(3, Date.valueOf(((JobPA) job).getDecreeDate()));
				st.setString(4, job.getId());

				st.executeUpdate();

				conn.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}

		System.out.println("Rows updated JOBS => " + rowsAffected);

	}

	public void updateNote(String accountID, String note) {
		String query = "INSERT INTO deadlines.rspp_notes " + "(fiscalcode, notes) VALUES ( ? , ? ) "
				+ "ON CONFLICT (fiscalcode) DO UPDATE SET notes = ? ";

		int rowsAffected = 0;

		try {
			Connection conn = ConnectDB.getConnection(session, config);
			PreparedStatement st = conn.prepareStatement(query);

			st.setString(1, accountID);
			st.setString(2, note);
			st.setString(3, note);

			rowsAffected = st.executeUpdate();

			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
		System.out.println("Rows updated NOTES => " + rowsAffected);
	}

	public void updateRSPP(String jobCode, LocalDate oldJobStart, RSPP rspp) {
		String query = "update deadlines.rspp set jobstart = ? , jobend = ? "
				+ "where rspp_jobid = ? and jobstart = ? ";

		int rowsAffected = 0;

		try {
			Connection conn = ConnectDB.getConnection(session, config);
			PreparedStatement st = conn.prepareStatement(query);

			st.setDate(1, Date.valueOf(rspp.getStart()));
			st.setDate(2, Date.valueOf(rspp.getEnd()));
			st.setString(3, jobCode);
			st.setDate(4, Date.valueOf(oldJobStart));

			rowsAffected = st.executeUpdate();

			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}

		System.out.println("Rows updated RSPP => " + rowsAffected);
	}

	public int updateInvoice(String oldInvoiceID, Invoice invoice) {
		String query = "update invoices.invoices set invoiceid = ? , \"number\" = ? , emission = ? , \"type\" = ? , payed = ? where invoiceid = ? ";
		int rowsAffected = 0;

		try {
			Connection conn = ConnectDB.getConnection(session, config);
			PreparedStatement st = conn.prepareStatement(query);

			st.setString(1, invoice.getId());
			st.setInt(2, invoice.getNumber());
			st.setDate(3, Date.valueOf(invoice.getEmission()));
			st.setString(4, invoice.getType());
			st.setBoolean(5, invoice.getPayed());
			st.setString(6, oldInvoiceID);

			rowsAffected = st.executeUpdate();

			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
		System.out.println("Rows updated INVOICE => " + rowsAffected);
		return rowsAffected;
	}

	public int newInvoice(Invoice invoice) {
		String query = "insert into invoices.invoices (invoiceid, \"number\", emission, \"type\", payed) values ( ? , ? , ? , ? , ? ) ";
		int rowsAffected = 0;

		try {
			Connection conn = ConnectDB.getConnection(session, config);
			PreparedStatement st = conn.prepareStatement(query);

			st.setString(1, invoice.getId());
			st.setInt(2, invoice.getNumber());
			st.setDate(3, Date.valueOf(invoice.getEmission()));
			st.setString(4, invoice.getType());
			st.setBoolean(5, invoice.getPayed());

			rowsAffected = st.executeUpdate();

			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
		System.out.println("Rows updated INVOICE => " + rowsAffected);
		return rowsAffected;
	}

	public int matchRSPPInvoice(RSPP rspp, Invoice invoice, String description) {
		String query = "insert into deadlines.rspp_invoices (rspp_id, rspp_start, invoice_id, description) values ( ? , ? , ? , ? ) ";
		int rowsAffected = 0;

		try {
			Connection conn = ConnectDB.getConnection(session, config);
			PreparedStatement st = conn.prepareStatement(query);

			st.setString(1, rspp.getJob().getId());
			st.setDate(2, Date.valueOf(rspp.getStart()));
			st.setString(3, invoice.getId());
			if (description == null || description.isEmpty())
				st.setString(4, null);
			else
				st.setString(4, description);

			rowsAffected = st.executeUpdate();

			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
		System.out.println("Rows updated JOBS_INVOICE => " + rowsAffected);
		return rowsAffected;
	}

	public int newAccount(Account account) {
		String query = "insert into accounts.accounts "
				+ "(fiscalcode,\"name\",numbervat,atecocode,legal_address,customer_category,descriptor) "
				+ "values ( ? , ? , ? , ? , ? , ? , ? ) ";

		int rowsAffected = 0;

		try {
			Connection conn = ConnectDB.getConnection(session, config);
			PreparedStatement st = conn.prepareStatement(query);

			st.setString(1, account.getFiscalCode());
			st.setString(2, account.getName());
			st.setString(3, account.getNumberVAT());
			st.setString(4, account.getAtecoCode());
			st.setString(5, account.getLegalAddress());
			st.setString(6, account.getCategory());
			st.setString(7, account.getDescriptor());

			rowsAffected = st.executeUpdate();

			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database o campo già esistente");
			return -1;
		}

		System.out.println("Rows updated ACCOUNT => " + rowsAffected);
		return 0;
	}

	public List<Account> getAllAccounts() {
		String sql = "select * from accounts.accounts order by \"name\"";

		try {
			Connection conn = ConnectDB.getConnection(session, config);
			PreparedStatement st = conn.prepareStatement(sql);

			ResultSet res = st.executeQuery();

			return resToAccounts(res);

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}

	public List<Job> getAllJobOfAccount(Account account) {
		List<Job> jobs = new LinkedList<Job>();
		try {
			Connection conn = ConnectDB.getConnection(session, config);
			PreparedStatement st = conn.prepareStatement(
					"select j.jobs_id, jobs_category, jobs_type, jobs_description, cig, decree_number, decree_date from jobs.jobs j left join jobs.jobs_pa jp on j.jobs_id = jp.jobs_id where customer = ? ");

			st.setString(1, account.getFiscalCode());
			ResultSet res = st.executeQuery();

			while (res.next()) {
				Job job = new Job(res.getString("jobs_id"), res.getString("jobs_category"), res.getString("jobs_type"),
						res.getString("jobs_description"), account);

				if (account.getCategory().equals("pa")) {
					if (res.getDate("decree_date") == null)
						job = new JobPA(job, res.getString("cig"), res.getInt("decree_number"), null);
					else
						job = new JobPA(job, res.getString("cig"), res.getInt("decree_number"),
								res.getDate("decree_date").toLocalDate());
				}
				jobs.add(job);
			}
			return jobs;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
		}
		return null;
	}

	public int newJob(Job job) {
		try {
			Connection conn = ConnectDB.getConnection(session, config);
			PreparedStatement st = conn.prepareStatement(
					"insert into jobs.jobs (jobs_id, jobs_category, jobs_type, jobs_description, customer) "
							+ "values ( ? , ? , ? , ? , ? ) ");

			st.setString(1, job.getId());
			st.setString(2, job.getJobCategory());
			st.setString(3, job.getJobType());
			st.setString(4, job.getDescription());
			st.setString(5, job.getCustomer().getFiscalCode());

			st.executeUpdate();

			if (job instanceof JobPA) {
				st = conn.prepareStatement("insert into jobs.jobs_pa (jobs_id, cig, decree_number, decree_date) "
						+ "values ( ? , ? , ? , ? ) ");

				st.setString(1, job.getId());
				st.setString(2, ((JobPA) job).getCig());
				st.setInt(3, ((JobPA) job).getDecreeNumber());
				st.setDate(4, Date.valueOf(((JobPA) job).getDecreeDate()));

				st.executeUpdate();
			}

			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database o campo già esistente");
			return -1;
		}
		return 0;
	}

	public int newRSPP(RSPP rspp) {
		String query = "insert into deadlines.rspp (rspp_jobid, jobstart, jobend) values (?, ?, ?)";

		int rowsAffected = 0;

		try {
			Connection conn = ConnectDB.getConnection(session, config);
			PreparedStatement st = conn.prepareStatement(query);

			st.setString(1, rspp.getJob().getId());
			st.setDate(2, Date.valueOf(rspp.getStart()));
			st.setDate(3, Date.valueOf(rspp.getEnd()));

			rowsAffected = st.executeUpdate();

			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database o campo già esistente");
			return -1;
		}

		System.out.println("Rows updated ACCOUNT => " + rowsAffected);
		return 0;
	}

	public Account getAccountFromVATNumber(String vatNumber) {
		String query = "select * from accounts.accounts a where a.numbervat = ? ";
		Account account;

		try {
			Connection conn = ConnectDB.getConnection(session, config);
			PreparedStatement st = conn.prepareStatement(query);

			st.setString(1, vatNumber);

			ResultSet res = st.executeQuery();
			if (res.next() == false)
				return null;
			account = resToAccounts(res).get(0);
			return account;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}

	public Account getAccountOfInvoice(Invoice invoice) {
		String query = "select a.fiscalcode, a.\"name\", a.numbervat, a.atecocode, a.legal_address, a.customer_category, a.descriptor "
				+ "from accounts.accounts a, jobs.jobs j, invoices.invoices i, deadlines.rspp r "
				+ "where a.fiscalcode = j.customer and r.invoiceid = i.invoiceid and r.rspp_jobid = j.jobs_id and "
				+ "i.\"number\" = ? and i.\"type\" = ? and date_part('year', emission) = ? ";
		Account account;

		try {
			Connection conn = ConnectDB.getConnection(session, config);
			PreparedStatement st = conn.prepareStatement(query);

			st.setInt(1, invoice.getNumber());
			st.setString(2, invoice.getType());
			st.setInt(3, invoice.getEmission().getYear());

			ResultSet res = st.executeQuery();
			if (res.next() == false)
				return null;
			account = resToAccounts(res).get(0);
			return account;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}
}