package dclient.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class JobPA extends Job {
	String cig;
	Integer decreeNumber;
	LocalDate decreeDate;

	public JobPA(Job job, String cig, int decreeNumber, LocalDate decreeDate) {
		super(job.getId(), job.getJobCategory(), job.getJobType(), job.getDescription(), job.getCustomer());
		this.cig = cig;
		this.decreeNumber = decreeNumber;
		this.decreeDate = decreeDate;

		trimAccountString();
	}

	public JobPA(Connection conn, ResultSet res) {
		super(conn, res);
		try {
			this.cig = res.getString("cig");

			if (res.getInt("decree_number") != 0)
				this.decreeNumber = res.getInt("decree_number");
			else
				this.decreeNumber = null;

			if (res.getDate("decree_date") != null)
				this.decreeDate = res.getDate("decree_date").toLocalDate();
			else
				this.decreeDate = null;
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public JobPA(Job job, String cig, String decreeNumber, LocalDate decreeDate) {
		super(job.getId(), job.getJobCategory(), job.getJobType(), job.getDescription(), job.getCustomer());
		this.cig = cig;
		this.decreeDate = decreeDate;
		try {
			this.decreeNumber = Integer.parseInt(decreeNumber);
		} catch (NumberFormatException e) {
			this.decreeNumber = null;
		}
	}

	public String getCig() {
		return cig;
	}

	public Integer getDecreeNumber() {
		return decreeNumber;
	}

	public String getDecreeNumberString() {
		try{
			return decreeNumber.toString();
		} catch (NullPointerException npe){
			return null;
		}
	}

	public LocalDate getDecreeDate() {
		return decreeDate;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((cig == null) ? 0 : cig.hashCode());
		result = prime * result + ((decreeDate == null) ? 0 : decreeDate.hashCode());
		result = prime * result + ((decreeNumber == null) ? 0 : decreeNumber.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		JobPA other = (JobPA) obj;
		if (cig == null) {
			if (other.cig != null)
				return false;
		} else if (!cig.equals(other.cig))
			return false;
		if (decreeDate == null) {
			if (other.decreeDate != null)
				return false;
		} else if (!decreeDate.equals(other.decreeDate))
			return false;
		if (decreeNumber == null) {
			if (other.decreeNumber != null)
				return false;
		} else if (!decreeNumber.equals(other.decreeNumber))
			return false;
		return true;
	}

	private void trimAccountString() {
		if (id != null)
			id = id.trim();

		if (description != null)
			description = description.trim();

		if (cig != null)
			cig = cig.trim();
	}
}
