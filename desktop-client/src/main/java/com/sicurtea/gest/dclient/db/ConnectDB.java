package com.sicurtea.gest.dclient.db;

import java.sql.Connection;
import java.sql.DriverManager;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class ConnectDB {
    static String CONFIG_FILE_NAME = "dbSetting.yaml";

    public static void main(String args[]) {
        Connection conn = null;
        Session session = null;

        String dbHost = null;
        int dbPort = 0;
        String dbUser = null;
        String dbPassword = null;
        String dbName = null;

        String sshUser = null;
        String sshHost = null;
        int sshPort = 0;
        String sshPassword = null;

        //TODO reading file variables from YAML config

        try {
            // SSH connection setup && port forwarding
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no"); // Set StrictHostKeyChecking property to no to avoid
                                                       // UnknownHostKey issue
            JSch jsch = new JSch();
            session = jsch.getSession(sshUser, sshHost, sshPort);
            session.setPassword(sshPassword);
            session.setConfig(config);
            session.connect();
            System.out.println("Connected");
            int assinged_port = session.setPortForwardingL(dbPort, dbHost, dbPort);
            System.out.println("localhost:" + assinged_port + " -> " + dbPort + ":" + dbPort);
            System.out.println("Port Forwarded");

            Class.forName("org.postgresql.Driver");

            conn = DriverManager.getConnection(
                    "jdbc:postgresql://" + dbHost + ":" + dbPort + "/" + dbName, dbUser,
                    dbPassword);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println("Opened database successfully");
    }
}