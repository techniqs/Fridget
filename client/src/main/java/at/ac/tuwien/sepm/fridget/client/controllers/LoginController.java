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
public class LoginController {

    @Autowired
    UIService uiService;

    @Autowired
    UserService userService;

    @Autowired
    private IAuthenticationService authenticationService;


    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;


    public void handleLogin(ActionEvent actionEvent) {
        try {
            UserValidator.getInstance().validateEmail(emailField.getText());
            UserValidator.getInstance().validatePasswordForLogin(passwordField.getText());
            try {
                User user = userService.findUser(getCredentials());
                if (user != null) {
                    authenticationService.setStoredCredentials(getCredentials());
                    authenticationService.setCachedUser(user);
                    uiService.handleLoginCompleted();
                } else {
                    AlertHelper.alert(Alert.AlertType.ERROR, "Invalid Credentials", "The given credentials are invalid. Please check your email and retype your password.");
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

    public void handleRegister(ActionEvent actionEvent) {
        uiService.goToScene(FridgetScene.REGISTER);
    }

    public void handleForgotPassword(ActionEvent actionEvent) {
        uiService.goToScene(FridgetScene.FORGOT_PASSWORD);
    }


    private UserCredentials getCredentials() {
        return new UserCredentials(emailField.getText(), PasswordHelpers.hashOnClient(passwordField.getText()));
    }

    public void handleSubmit(ActionEvent actionEvent) {
        if (emailField.getText().isEmpty()) {
            emailField.requestFocus();
        } else if (passwordField.getText().isEmpty()) {
            passwordField.requestFocus();
        } else {
            handleLogin(actionEvent);
        }
    }

}
