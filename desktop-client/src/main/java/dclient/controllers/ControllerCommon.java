package dclient.controllers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import dclient.model.Model;
import javafx.scene.control.DatePicker;

public class ControllerCommon {
    /**
     * This function is meant to handle datepicker fields when the date is added
     * manually instead using the picker. If the date is manually entered has higher
     * priority over the picker.
     * 
     * @param model      the Model of the program (used to fetch the date format
     *                   used in the program).
     * @param datePicker the DatePicker from whom we need the pick the date
     * @return a LocalDate object of the date in the Datepicker giving priority to
     *         text over value.
     */
    public static LocalDate getDate(Model model, DatePicker datePicker) {
        LocalDate date;
        try {
            date = LocalDate.parse(datePicker.getEditor().getText(),
                    DateTimeFormatter.ofPattern(model.getConfig().getProperty("dateFormat", "dd/MM/yyyy")));
        } catch (DateTimeParseException e) {
            date = null;
        }
        if (date != null)
            return date;
        else
            return datePicker.getValue();
    }
}
