package com.sicurtea.gest.dclient.db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.util.Properties;

public class ConnectDB {
    static String CONFIG_FILE_NAME = "dbConfig.properties";

    public static Connection getConnection() {
        LoaderDBConf dbc = new LoaderDBConf(CONFIG_FILE_NAME);

        Connection conn = null;
        Session session = null;

        try {
            // SSH connection setup && port forwarding
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no"); // Set StrictHostKeyChecking property to no to avoid
                                                       // UnknownHostKey issue
            JSch jsch = new JSch();
            session = jsch.getSession(dbc.getSshUser(), dbc.getSshHost(), dbc.getSshPort());
            session.setPassword(dbc.getSshPassword());
            session.setConfig(config);
            session.connect();
            System.out.println("Connected");
            int assinged_port = session.setPortForwardingL(dbc.getDbPort(), dbc.getDbHost(), dbc.getDbPort());
            System.out.println("localhost:" + assinged_port + " -> " + dbc.getDbPort() + ":" + dbc.getDbPort());
            System.out.println("Port Forwarded");

            Class.forName("org.postgresql.Driver");

            conn = DriverManager.getConnection(
                    "jdbc:postgresql://" + dbc.getDbHost() + ":" + dbc.getDbPort() + "/" + dbc.getDbName(),
                    dbc.getDbUser(), dbc.getDbPassword());
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        System.out.println("Opened database successfully");
        return conn;
    }

    private static class LoaderDBConf {
        Properties dbConfig;

        String dbHost;
        int dbPort;
        String dbUser;
        String dbPassword;
        String dbName;

        String sshUser;
        String sshHost;
        int sshPort;
        String sshPassword;

        public LoaderDBConf(String fileName) {
            dbConfig = new Properties();
            try {
                dbConfig.load(this.getClass().getResourceAsStream(fileName));

                dbHost = dbConfig.getProperty("dbHost");
                dbPort = Integer.parseInt(dbConfig.getProperty("dbPort"));
                dbUser = dbConfig.getProperty("dbUser");
                dbPassword = dbConfig.getProperty("dbHost");
                dbName = dbConfig.getProperty("dbName");

                sshUser = dbConfig.getProperty("sshUser");
                sshHost = dbConfig.getProperty("sshHost");
                sshPort = Integer.parseInt(dbConfig.getProperty("sshPort"));
                sshPassword = dbConfig.getProperty("sshPassword");
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

        public String getSshPassword() {
            return this.sshPassword;
        }
    }
}