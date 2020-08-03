package dclient.db;

import java.util.Properties;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

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
        }
        return session;
    }
}