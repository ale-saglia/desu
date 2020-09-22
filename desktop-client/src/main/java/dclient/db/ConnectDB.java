package dclient.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

import com.jcraft.jsch.Session;

public class ConnectDB {
	public static Connection getConnection(Session session, Properties config) {
		Connection conn = null;

		try {
			// DB connection
			Class.forName("org.postgresql.Driver");
			String dbString = "jdbc:postgresql://" + config.getProperty("db.host") + ":"
					+ session.setPortForwardingL(0, config.getProperty("db.host"), Integer.parseInt(config.getProperty("db.port"))) + "/" + config.getProperty("db.database");

			conn = DriverManager.getConnection(dbString, (config.getProperty("db.user")), (config.getProperty("db.password")));

		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
		return conn;
	}
}