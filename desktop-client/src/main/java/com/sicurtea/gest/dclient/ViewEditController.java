package com.sicurtea.gest.dclient;

import java.io.IOException;
import javafx.fxml.FXML;

public class ViewEditController {

    @FXML
    private void switchToPrimary() throws IOException {
        App.setRoot("main");
    }
}