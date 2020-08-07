module dclient {
    requires transitive javafx.controls;
    requires javafx.fxml;
    requires org.postgresql.jdbc;
    requires java.sql;
    requires jsch;
    requires transitive jasypt;
	requires javafx.graphics;
	requires org.apache.commons.lang3;
    
    opens dclient.controllers;
    exports dclient;
    exports dclient.model;
}