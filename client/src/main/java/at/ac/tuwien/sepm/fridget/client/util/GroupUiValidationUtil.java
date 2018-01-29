package at.ac.tuwien.sepm.fridget.client.util;

import at.ac.tuwien.sepm.fridget.common.entities.Group;
import javafx.scene.control.Alert;

//TODO Exception Handling changing to AlertClass!


public class GroupUiValidationUtil {


    /**
     * Basic UI validation of group
     *
     * @return true if all required fields are set and valid, false otherwise
     */
    public static boolean uiValidation(Group group) {
        StringBuilder errors = new StringBuilder();
        if (group.getName() == null) {
            errors.append("Please specify a groupname").append(System.lineSeparator());
        } else if (group.getName().isEmpty()) {
            errors.append("Invalid Groupname").append(System.lineSeparator());
        }

        if (!errors.toString().equals("")) {
            AlertHelper.invalidArgumentExceptionAlert(errors.toString());
            return false;
        }
        return true;
    }

}

