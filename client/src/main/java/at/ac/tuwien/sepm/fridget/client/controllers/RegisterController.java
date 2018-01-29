package at.ac.tuwien.sepm.fridget.client.controllers;

import at.ac.tuwien.sepm.fridget.client.services.IAuthenticationService;
import at.ac.tuwien.sepm.fridget.client.services.UIService;
import at.ac.tuwien.sepm.fridget.client.util.AlertHelper;
import at.ac.tuwien.sepm.fridget.client.util.FridgetScene;
import at.ac.tuwien.sepm.fridget.common.entities.User;
import at.ac.tuwien.sepm.fridget.common.entities.UserCredentials;
import at.ac.tuwien.sepm.fridget.common.exception.InvalidArgumentException;
import at.ac.tuwien.sepm.fridget.common.exception.PersistenceException;
import at.ac.tuwien.sepm.fridget.common.services.UserService;
import at.ac.tuwien.sepm.fridget.common.util.PasswordHelpers;
import at.ac.tuwien.sepm.fridget.common.util.UserValidator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class RegisterController {

    @Autowired
    UIService uiService;

    @Autowired
    UserService userService;

    @Autowired
    private IAuthenticationService authenticationService;


    @FXML
    private TextField nameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;


    public void handleRegister(ActionEvent actionEvent) {
        User user = new User();
        user.setName(nameField.getText());
        user.setEmail(emailField.getText());
        user.setPassword(passwordField.getText());
        try {
            UserValidator.getInstance().validateUserForRegistrationClient(user);
            user.setPassword(PasswordHelpers.hashOnClient(user.getPassword()));
            try {
                user = userService.createUser(user);
                if (user != null) {
                    authenticationService.setStoredCredentials(getCredentials());
                    uiService.handleLoginCompleted();
                } else {
                    AlertHelper.alert(Alert.AlertType.ERROR, "Unexpected Error", "An error occured while trying to register the account. Please make sure the email is not already taken and try again.");
                }
            } catch (PersistenceException e) {
                AlertHelper.persistenceExceptionAlert(e.getMessage());
            } catch (InvalidArgumentException e) {
                AlertHelper.invalidArgumentExceptionAlert(e.getMessage());
            }
        } catch (InvalidArgumentException e) {
            AlertHelper.invalidArgumentExceptionAlert(e.getMessage());
        }
    }

    public void handleBack(ActionEvent actionEvent) {
        uiService.goToScene(FridgetScene.LOGIN);
    }

    public void handleSubmit(ActionEvent actionEvent) {
        if (nameField.getText().isEmpty()) {
            nameField.requestFocus();
        } else if (emailField.getText().isEmpty()) {
            emailField.requestFocus();
        } else if (passwordField.getText().isEmpty()) {
            passwordField.requestFocus();
        } else {
            handleRegister(actionEvent);
        }
    }


    private UserCredentials getCredentials() {
        return new UserCredentials(emailField.getText(), PasswordHelpers.hashOnClient(passwordField.getText()));
    }

}
