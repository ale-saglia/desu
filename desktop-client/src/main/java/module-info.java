module com.sicurtea.gest.dclient {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.sicurtea.gest.dclient to javafx.fxml;
    exports com.sicurtea.gest.dclient;
}