package at.ac.tuwien.sepm.fridget.client.util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

/**
 * AlertHelper for simplifying Ui-Exceptionhandling
 */
public class AlertHelper {

    /**
     * Method to create own Alert
     *
     * @param type    Type of the Alert
     * @param title   Title of the Alert
     * @param message Message of the Alert
     * @return The AlertMessage
     */
    public static Optional<ButtonType> alert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setTitle(title);
        alert.setContentText(message);


        return alert.showAndWait();

    }

    /**
     * Alert for the ioException
     *
     * @param exceptionMessage Message of the Exception
     */
    public static void ioExceptionAlert(String exceptionMessage) {
        alert(Alert.AlertType.ERROR, "File System Error", exceptionMessage);
    }

    /**
     * Alert for the persistenceException
     *
     * @param exceptionMessage Message of the Exception
     */
    public static void persistenceExceptionAlert(String exceptionMessage) {
        alert(Alert.AlertType.ERROR, "Internal Server Error", exceptionMessage);
    }

    /**
     * Alert for the invalidArgumentException
     *
     * @param exceptionMessage Message of the Exception
     */
    public static void invalidArgumentExceptionAlert(String exceptionMessage) {
        alert(Alert.AlertType.ERROR, "Invalid Values", exceptionMessage);
    }

    /**
     * Build and show a success message dialog
     *
     * @param message the success message to display
     */
    public static Optional<ButtonType> successAlert(String message) {
        return alert(Alert.AlertType.INFORMATION, "Success", message);
    }
}
