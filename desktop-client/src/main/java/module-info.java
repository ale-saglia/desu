module dclient {
	requires transitive javafx.controls;
	requires javafx.fxml;

	requires org.postgresql.jdbc;
	requires java.sql;
	requires jsch;
	requires transitive jasypt;
	requires javafx.graphics;
	requires org.apache.commons.lang3;
	requires com.google.common;
	requires org.apache.commons.text;
	requires java.logging;
	requires org.apache.commons.io;
	requires javafx.base;

	opens dclient.controllers to javafx.fxml, javafx.base;
	opens dclient.controllers.visualModels to javafx.fxml, javafx.base;

	exports dclient;
}