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
		String sql = Queries.getRSPPtable();
		
		List<Map<String, String>> tableElements = new ArrayList<Map<String, String>>();
		Map<String, String> temp;
		
		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();

			while (res.next()) {
				temp = new HashMap<String, String>();
				temp.put("name", res.getString("name"));
				temp.put("category", res.getString("category"));
				temp.put("jobend", (new SimpleDateFormat("dd/MM/yyyy")).format(res.getDate("jobend")));
				temp.put("invoiceid", res.getString("invoiceid"));
				temp.put("payed", String.valueOf(res.getBoolean("payed")));
				
				//Useful for tracking selected field when view / edit
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
}