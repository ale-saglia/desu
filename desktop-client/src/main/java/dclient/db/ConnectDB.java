package dclient.db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class ConnectDB {
	static String CONFIG_FILE_NAME = "config.properties";

	public static Session getSession() {
		JSch jsch = new JSch();
		Session session = null;
		String privateKeyPath = System.getProperty("user.home") + "/.ssh/id_rsa";
		LoaderDBConf dbc = (new ConnectDB()).new LoaderDBConf(CONFIG_FILE_NAME);

		try {
			// SSH connection setup && port forwarding
			jsch.addIdentity(privateKeyPath, dbc.getSshKeyPassword());
			session = jsch.getSession(dbc.getSshUser(), dbc.getSshHost(), dbc.getSshPort());
			session.setConfig("PreferredAuthentications", "publickey,keyboard-interactive,password");

			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");

			session.setConfig(config);
			session.connect();
			System.out.println("Connected");

			System.out.println(session.getPortForwardingL());

			System.out.println("localhost:" + session.setPortForwardingL(0, dbc.getDbHost(), dbc.getDbPort()) + " -> "
					+ dbc.getDbPort());
			System.out.println("Port Forwarded");

		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
		return session;
	}

	public static Connection getConnection(Session session) {
		LoaderDBConf dbc = (new ConnectDB()).new LoaderDBConf(CONFIG_FILE_NAME);
		Connection conn = null;

		try {
			// DB connection
			Class.forName("org.postgresql.Driver");
			String dbString = "jdbc:postgresql://" + dbc.getDbHost() + ":"
					+ session.setPortForwardingL(0, dbc.getDbHost(), dbc.getDbPort()) + "/" + dbc.getDbName();

			conn = DriverManager.getConnection(dbString, (dbc.getDbUser()), (dbc.getDbPassword()));

		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
		System.out.println("Opened database successfully");
		return conn;
	}

	private class LoaderDBConf {
		Properties dbConfig;

		String dbHost;
		int dbPort;
		String dbUser;
		String dbPassword;
		String dbName;

		String sshUser;
		String sshHost;
		int sshPort;
		String sshKeyPassword;

		public LoaderDBConf(String fileName) {
			// TODO Find a way to encrypt properties file for realease.
			dbConfig = new Properties();
			try {
				dbConfig.load(ConnectDB.class.getResourceAsStream(fileName));

				dbHost = dbConfig.getProperty("dbHost");
				dbPort = Integer.parseInt(dbConfig.getProperty("dbPort"));
				dbUser = dbConfig.getProperty("dbUser");
				dbPassword = dbConfig.getProperty("dbPassword");
				dbName = dbConfig.getProperty("dbName");

				sshUser = dbConfig.getProperty("sshUser");
				sshHost = dbConfig.getProperty("sshHost");
				sshPort = Integer.parseInt(dbConfig.getProperty("sshPort"));
				sshKeyPassword = dbConfig.getProperty("sshKeyPassword");
			} catch (IOException e) {
				e.printStackTrace();
			} catch (NumberFormatException nfm) {
				System.out.println("Error in configuration file, missing or invalid field.");
				nfm.printStackTrace();
			}
		}

		public String getDbHost() {
			return this.dbHost;
		}

		public int getDbPort() {
			return this.dbPort;
		}

		public String getDbUser() {
			return this.dbUser;
		}

		public String getDbPassword() {
			return this.dbPassword;
		}

		public String getDbName() {
			return this.dbName;
		}

		public String getSshUser() {
			return this.sshUser;
		}

		public String getSshHost() {
			return this.sshHost;
		}

		public int getSshPort() {
			return this.sshPort;
		}

		public String getSshKeyPassword() {
			return this.sshKeyPassword;
		}
	}
}