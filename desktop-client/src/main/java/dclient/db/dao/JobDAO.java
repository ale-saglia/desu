package dclient.db.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import dclient.model.Account;
import dclient.model.Job;
import dclient.model.JobPA;

public class JobDAO {
	public static Collection<String> getJobCategories(Connection conn) {
		String sql = "select * from jobs.jobs_categories jc";
		List<String> categories = new ArrayList<String>();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next())
				categories.add(res.getString("categories"));
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}

		if (categories.size() > 0)
			return categories;
		else
			return null;
	}

	public static Collection<String> getJobTypes(Connection conn) {
		String sql = "select * from jobs.job_types jt ";
		List<String> types = new ArrayList<String>();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next())
				types.add(res.getString("types"));
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}

		if (types.size() > 0)
			return types;
		else
			return null;
	}

	public static int newJob(Connection conn, Job job) {
		String query = "insert into jobs.jobs (jobs_id, jobs_category, jobs_type, jobs_description, customer) values ( ? , ? , ? , ? , ? ) ";
		int rowsAffected = 0;

		try {
			PreparedStatement st = conn.prepareStatement(query);
			st.setString(1, job.getId());
			st.setString(2, job.getJobCategory());
			st.setString(3, job.getJobType());
			st.setString(4, job.getDescription());
			st.setString(5, job.getCustomer().getFiscalCode());
			rowsAffected += st.executeUpdate();

			if (job instanceof JobPA) {
				query = "insert into jobs.jobs_pa (jobs_id, cig, decree_number, decree_date) values ( ? , ? , ? , ? ) ";
				st = conn.prepareStatement(query);
				st.setString(1, job.getId());
				st.setString(2, ((JobPA) job).getCig());
				st.setInt(3, ((JobPA) job).getDecreeNumber());
				st.setDate(4, Date.valueOf(((JobPA) job).getDecreeDate()));

				rowsAffected += st.executeUpdate();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database o campo già esistente");
		}
		return rowsAffected;
	}

	public static int updateJob(Connection conn, Job job, String oldJobCode) {
		String query = "update jobs.jobs " + "set jobs_id = ?, jobs_category = ?, jobs_type = ?, jobs_description = ? "
				+ "where jobs_id = ? ";
		int rowsAffected = 0;

		try {
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
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
		return rowsAffected;
	}

	public static Job getJob(Connection conn, String job_id) {
		String sql = "select * from jobs.jobs j left join jobs.jobs_pa jp on j.jobs_id = jp.jobs_id where j.jobs_id = ? ";
		Job job;

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, job_id);

			ResultSet res = st.executeQuery();
			res.next();
			job = new Job(conn, res);

			if (job.isJobCustomerPA())
				job = new JobPA(conn, job, res);
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
		return job;
	}

	public static Collection<Job> getJobs(Connection conn, Account account) {
		List<Job> jobs = new LinkedList<Job>();
		try {
			PreparedStatement st = conn.prepareStatement(
					"select j.jobs_id, jobs_category, jobs_type, jobs_description, customer, cig, decree_number, decree_date from jobs.jobs j left join jobs.jobs_pa jp on j.jobs_id = jp.jobs_id where customer = ? ");

			st.setString(1, account.getFiscalCode());
			ResultSet res = st.executeQuery();
			while (res.next())
				jobs.add(new Job(conn, res));
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
		}
		return jobs;
	}

	public static int addJobPAInfos(Connection conn, JobPA jobPA) {
		String query = "insert into jobs.jobs_pa (jobs_id, cig, decree_number, decree_date) values( ? , ? , ? , ? ) "
				+ "on conflict (jobs_id) do update set cig = ? , decree_number= ? , decree_date= ? ";

		int rowsAffected = 0;

		try {
			PreparedStatement st = conn.prepareStatement(query);

			st.setString(1, jobPA.getId());
			st.setString(2, jobPA.getCig());
			st.setInt(3, jobPA.getDecreeNumber());
			st.setDate(4, Date.valueOf(jobPA.getDecreeDate()));

			st.setString(5, jobPA.getCig());
			st.setInt(6, jobPA.getDecreeNumber());
			st.setDate(7, Date.valueOf(jobPA.getDecreeDate()));

			rowsAffected = st.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}

		System.out.println("Rows updated JOBS_PA => " + rowsAffected);
		return rowsAffected;
	}
}
