package dclient.db;

import java.util.Properties;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class ConnectSSH {
	public static Session getSession(Properties config) {
		JSch jsch = new JSch();
		Session session = null;
		String privateKeyPath = System.getProperty("user.home") + "/.ssh/" + config.getProperty("ssh.keyName");

		try {
			// SSH connection setup && port forwarding
			jsch.addIdentity(privateKeyPath, config.getProperty("ssh.keyPassword"));
			session = jsch.getSession(config.getProperty("ssh.user"), config.getProperty("ssh.host"),
					Integer.parseInt(config.getProperty("ssh.port")));
			session.setConfig("PreferredAuthentications", "publickey,keyboard-interactive,password");

			Properties sshConnConfig = new java.util.Properties();
			sshConnConfig.put("StrictHostKeyChecking", "no");

			session.setConfig(sshConnConfig);
			session.connect();

			System.out.println("Connected");

			System.out.println(session.getPortForwardingL());

			System.out
					.println("localhost:"
							+ session.setPortForwardingL(0, config.getProperty("ssh.host"),
									Integer.parseInt(config.getProperty("db.port")))
							+ " -> " + config.getProperty("db.port"));
			System.out
					.println("localhost:"
							+ session.setPortForwardingL(0, config.getProperty("ssh.host"),
									Integer.parseInt(config.getProperty("db.port")))
							+ " -> " + config.getProperty("db.port"));
			System.out.println("Port Forwarded");

		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(e.getClass().getName() + ": " + e.getMessage());

			final Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Impossibile connettersi al database!");
			alert.setHeaderText("Non è stato possibile connettersi al database");
			alert.setContentText("Questo potrebbe essere causato da uno dei seguenti scenari:\n\r "
					+ "- La password non è stata inserita correttamente\n\r"
					+ "- Non è possibile connettersi al server\n\r"
					+ "- I dati nel file di configurazione non sono corretti");
			alert.showAndWait();
		}
		return session;
	}
}