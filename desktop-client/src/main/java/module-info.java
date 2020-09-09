module dclient {
	requires transitive javafx.controls;
	requires javafx.fxml;

	requires org.postgresql.jdbc;
	requires java.sql;
	requires jsch;
	requires transitive jasypt;
	requires javafx.graphics;
	requires com.google.common;
	requires java.logging;
	requires javafx.base;

	opens dclient.controllers to javafx.fxml, javafx.base;
	opens dclient.controllers.visualModels to javafx.fxml, javafx.base;

	exports dclient;
}