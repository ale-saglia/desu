package com.sicurtea.gest.dclient.db;

import java.sql.Connection;
import java.sql.DriverManager;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class ConnectDB {
    //mvn dependency:sourcesprivate static final String jdbcURL = 

    public static void main(String args[]) {
        Connection c = null;
        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/testdb", "postgres", "123");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println("Opened database successfully");
    }
}