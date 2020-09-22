package dclient.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class ConMan {
	Properties config;
	Session session;
	private Connection tempConnection;

	public ConMan(Properties config) {
		this.config = config;
		this.session = ConnectSSH.getSession(config);
		this.tempConnection = ConnectDB.getConnection(session, config);
	}

	public Session getSession() {
		return session;
	}

	public void newSession() {
		this.session = ConnectSSH.getSession(config);
	}

	public String closeSession() {
		closeDBConnection();
		
		String message = null;
		try {
			message = "Closing: " + session.getPortForwardingL();
		} catch (JSchException e) {
			e.printStackTrace();
		}
		this.session.disconnect();
		return message;
	}
	
	public Connection getDBConnection() {
		try {
			if(tempConnection.isClosed())
				tempConnection = ConnectDB.getConnection(session, config);
			return tempConnection;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void closeDBConnection() {
		try {
			if(!tempConnection.isClosed())
				tempConnection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
