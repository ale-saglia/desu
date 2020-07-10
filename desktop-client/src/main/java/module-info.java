module dclient {
    requires javafx.controls;
    requires javafx.fxml;

    opens dclient to javafx.fxml;
    exports dclient;
}