module com.sicurtea.gest.dclient {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.sicurtea.gest.dclient to javafx.fxml;
    exports com.sicurtea.gest.dclient;
}