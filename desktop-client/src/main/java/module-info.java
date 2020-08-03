module dclient {
    requires transitive javafx.controls;
    requires javafx.fxml;
    requires org.postgresql.jdbc;
    requires java.sql;
    requires jsch;
    requires transitive jasypt;
	requires javafx.graphics;
    
    opens dclient.controllers;
    exports dclient;
    exports dclient.model;
}