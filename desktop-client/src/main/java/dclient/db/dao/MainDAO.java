package dclient.db.dao;

import java.util.Properties;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import dclient.db.ConnectSSH;

public class MainDAO {
	Properties config;
	Session session;

	public MainDAO(Properties config) {
		this.config = config;
		this.session = ConnectSSH.getSession(config);
	}

	public Session getSession() {
		return session;
	}

	public void newSession() {
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
}
