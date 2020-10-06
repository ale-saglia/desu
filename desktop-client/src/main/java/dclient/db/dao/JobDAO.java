package dclient.db.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dclient.model.Account;
import dclient.model.Job;
import dclient.model.JobPA;

public class JobDAO {
	private static Logger logger = LoggerFactory.getLogger("DClient");

	public static Map<String, Set<String>> getJobCategories(Connection conn) {
		String sql = "select * from jobs.job_types";
		Map<String, Set<String>> cat = new TreeMap<>();

		try (PreparedStatement st = conn.prepareStatement(sql); ResultSet res = st.executeQuery();) {
			while (res.next()) {
				String category = res.getString("category");

				if (!cat.containsKey(category)) {
					cat.put(category, new TreeSet<>());
				}
				cat.get(category).add(res.getString("types"));
			}
		} catch (SQLException e) {
			logger.error(e.getMessage());
		}
		return cat;
	}

	/**
	 * @param conn
	 * @param job
	 * @return
	 */
	public static int newJob(Connection conn, Job job) {
		String query = "insert into jobs.jobs (jobs_id, jobs_category, jobs_type, jobs_description, customer) values ( ? , ? , ? , ? , ? ) ";
		int rowsAffected = 0;

		try (PreparedStatement st = conn.prepareStatement(query);) {
			st.setString(1, job.getId());
			st.setString(2, job.getJobCategory());
			st.setString(3, job.getJobType());
			st.setString(4, job.getDescription());
			st.setString(5, job.getCustomer().getFiscalCode());
			rowsAffected += st.executeUpdate();

			if (job instanceof JobPA)
				rowsAffected += updateJobPA(conn, (JobPA) job);
		} catch (SQLException e) {
			logger.error(e.getMessage());
		}
		return rowsAffected;
	}

	/**
	 * @param conn
	 * @param job
	 * @param oldJobCode
	 * @return
	 */
	public static int updateJob(Connection conn, Job job, String oldJobCode) {
		String query = "update jobs.jobs " + "set jobs_id = ?, jobs_category = ?, jobs_type = ?, jobs_description = ? "
				+ "where jobs_id = ? ";
		int rowsAffected = 0;

		try (PreparedStatement st = conn.prepareStatement(query);) {
			st.setString(1, job.getId());
			st.setString(2, job.getJobCategory());
			st.setString(3, job.getJobType());
			st.setString(4, job.getDescription());
			st.setString(5, oldJobCode);

			rowsAffected = st.executeUpdate();

			if (job instanceof JobPA)
				rowsAffected += updateJobPA(conn, (JobPA) job);
		} catch (SQLException e) {
			logger.error(e.getMessage());
		}
		return rowsAffected;
	}

	private static int updateJobPA(Connection conn, JobPA job) {
		String newJobPA = "insert into jobs.jobs_pa (jobs_id, cig, decree_number, decree_date) values ( ? , ? , ? , ? ) ";
		String updateJobPA = "update jobs.jobs_pa set cig = ? , decree_number = ? , decree_date = ? where jobs_id = ? ";

		String query = newJobPA;
		int rowsAffected = 0;

		if (isJobPAExisting(conn, job))
			query = updateJobPA;

		try (PreparedStatement st = conn.prepareStatement(query);) {
			if (query.equals(newJobPA)) {
				st.setString(1, job.getId());
				st.setString(2, ((JobPA) job).getCig());
				st.setInt(3, ((JobPA) job).getDecreeNumber());
				st.setDate(4, Date.valueOf(((JobPA) job).getDecreeDate()));
			} else {
				st.setString(1, ((JobPA) job).getCig());
				st.setInt(2, ((JobPA) job).getDecreeNumber());
				st.setDate(3, Date.valueOf(((JobPA) job).getDecreeDate()));
				st.setString(4, job.getId());
			}

			rowsAffected += st.executeUpdate();
		} catch (SQLException e) {
			logger.error(e.getMessage());
		}
		return rowsAffected;
	}

	/**
	 * @param conn
	 * @param jobID
	 * @return
	 */
	public static Job getJob(Connection conn, String jobID) {
		String sql = "select * from jobs.jobs j left join jobs.jobs_pa jp on j.jobs_id = jp.jobs_id where j.jobs_id = ? ";
		Job job = null;

		try (PreparedStatement st = conn.prepareStatement(sql);) {
			st.setString(1, jobID);

			try (ResultSet res = st.executeQuery();) {
				res.next();
				job = new Job(conn, res);

				if (job.isJobCustomerPA())
					job = new JobPA(conn, res);
			}
		} catch (SQLException e) {
			logger.error(e.getMessage());
		}
		return job;
	}

	/**
	 * @param conn
	 * @param account
	 * @return
	 */
	public static Collection<Job> getJobs(Connection conn, Account account) {
		String query = "select j.jobs_id, jobs_category, jobs_type, jobs_description, customer, cig, decree_number, decree_date from jobs.jobs j left join jobs.jobs_pa jp on j.jobs_id = jp.jobs_id where customer = ? ";
		List<Job> jobs = new LinkedList<>();
		try (PreparedStatement st = conn.prepareStatement(query);) {
			st.setString(1, account.getFiscalCode());
			try (ResultSet res = st.executeQuery();) {
				while (res.next()) {
					if (account.getCategory().equals("pa"))
						jobs.add(new JobPA(conn, res));
					else
						jobs.add(new Job(conn, res));
				}
			}

		} catch (SQLException e) {
			logger.error(e.getMessage());
		}
		return jobs;
	}

	/**
	 * @param conn
	 * @param jobPA
	 * @return
	 */
	public static int addJobPAInfos(Connection conn, JobPA jobPA) {
		String query = "insert into jobs.jobs_pa (jobs_id, cig, decree_number, decree_date) values( ? , ? , ? , ? ) "
				+ "on conflict (jobs_id) do update set cig = ? , decree_number= ? , decree_date= ? ";

		int rowsAffected = 0;

		try (PreparedStatement st = conn.prepareStatement(query);) {
			st.setString(1, jobPA.getId());
			st.setString(2, jobPA.getCig());
			if (jobPA.getDecreeNumber() != null)
				st.setInt(3, jobPA.getDecreeNumber());
			else
				st.setNull(3, java.sql.Types.INTEGER);
			st.setDate(4, Date.valueOf(jobPA.getDecreeDate()));

			st.setString(5, jobPA.getCig());
			if (jobPA.getDecreeNumber() != null)
				st.setInt(6, jobPA.getDecreeNumber());
			else
				st.setNull(6, java.sql.Types.INTEGER);
			st.setDate(7, Date.valueOf(jobPA.getDecreeDate()));

			rowsAffected = st.executeUpdate();

		} catch (SQLException e) {
			logger.error(e.getMessage());
		}
		return rowsAffected;
	}

	public static Boolean isJobPAExisting(Connection conn, JobPA job) {
		String query = "select * from jobs.jobs_pa where jobs_id = ?";
		Boolean isJobPAExisting = null;

		try (PreparedStatement st = conn.prepareStatement(query);) {
			st.setString(1, job.getId());
			try (ResultSet res = st.executeQuery();) {
				isJobPAExisting = res.next();
			}
		} catch (SQLException e) {
			logger.error(e.getMessage());
		}
		return isJobPAExisting;
	}
}
