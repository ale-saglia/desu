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

public class SicurteaDAO {
	
	public List<Map<String, String>> getDataForTable(){
		String sql = Queries.getRSPPtable();/*"select *\n" + 
				"from (SELECT a.\"name\" as \"name\", a.customer_category as category, r.jobend as jobend, i.invoiceid as invoiceid, i.payed as payed\n" + 
				"FROM accounts.accounts a, deadlines.rspp r, invoices.invoices  i, jobs.jobs j\n" + 
				"union\n" + 
				"SELECT a.\"name\"as \"name\", a.customer_category as category, r.jobend as jobend, null as invoiceid, null as payed\n" + 
				"FROM accounts.accounts a, deadlines.rspp r, jobs.jobs j) as a";*/
		
		List<Map<String, String>> tableElements = new ArrayList<Map<String, String>>();
		Map<String, String> temp;
		
		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();

			while (res.next()) {
				temp = new HashMap<String, String>();
				temp.put("name", res.getString("name"));
				temp.put("category", res.getString("name"));
				temp.put("jobend", (new SimpleDateFormat("dd/MM/yyyy")).format(res.getDate("jobend")));
				temp.put("invoiceid", res.getString("invoiceid"));
				temp.put("payed", String.valueOf(res.getBoolean("payed")));
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
}