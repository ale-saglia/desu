package com.sicurtea.gest.dclient.db;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.util.Map;
import java.util.Properties;

import org.yaml.snakeyaml.Yaml;

public class ConnectDB {
    static String CONFIG_FILE_NAME = "dbSetting.yaml";

    public static Connection getConnection() {
        Properties dbConfig = new Properties();

        Connection conn = null;
        Session session = null;

        // TODO reading file variables from YAML config

        try {
            // SSH connection setup && port forwarding
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no"); // Set StrictHostKeyChecking property to no to avoid
                                                       // UnknownHostKey issue
            JSch jsch = new JSch();
            session = jsch.getSession(dbconf.sshUser, dbconf.sshHost, dbconf.sshPort);
            session.setPassword(dbconf.sshPassword);
            session.setConfig(config);
            session.connect();
            System.out.println("Connected");
            int assinged_port = session.setPortForwardingL(dbconf.dbPort, dbconf.dbHost, dbconf.dbPort);
            System.out.println("localhost:" + assinged_port + " -> " + dbconf.dbPort + ":" + dbconf.dbPort);
            System.out.println("Port Forwarded");

            Class.forName("org.postgresql.Driver");

            conn = DriverManager.getConnection(
                    "jdbc:postgresql://" + dbconf.dbHost + ":" + dbconf.dbPort + "/" + dbconf.dbName, dbconf.dbUser,
                    dbconf.dbPassword);
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
        String sshPassword;

        public LoaderDBConf(String fileName) {
            dbConfig = new Properties();
            try {
                dbConfig.load(this.getClass().getResourceAsStream("dbConfig.properties"));
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        public String getDBproperties(){
            
        }
    }
}