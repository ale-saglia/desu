module dclient {
    requires transitive javafx.controls;
    requires javafx.fxml;
    requires org.postgresql.jdbc;
    requires java.sql;
    requires jsch;
    
    opens dclient to javafx.fxml;
    exports dclient;
    exports dclient.model;
}