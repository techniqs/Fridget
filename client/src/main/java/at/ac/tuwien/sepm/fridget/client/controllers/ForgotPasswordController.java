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
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class ForgotPasswordController {

    @Autowired
    UIService uiService;

    @Autowired
    UserService userService;

    @Autowired
    private IAuthenticationService authenticationService;


    @FXML
    private TextField emailField;

    @FXML
    private TextField codeField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button sendResetCodeButton;

    @FXML
    private Button verifyCodeButton;

    @FXML
    private Button confirmButton;


    public void handleBack(ActionEvent actionEvent) {
        uiService.goToScene(FridgetScene.LOGIN);
    }

    public void handleSendResetCode(ActionEvent actionEvent) {
        try {
            UserValidator.getInstance().validateEmail(emailField.getText());

            userService.requestPasswordResetCode(emailField.getText());

            AlertHelper.successAlert("We've sent you an email with a reset password code, please check your inbox.");
            codeField.requestFocus();
        } catch (PersistenceException e) {
            AlertHelper.persistenceExceptionAlert(e.getMessage());
        } catch (InvalidArgumentException e) {
            AlertHelper.invalidArgumentExceptionAlert(e.getMessage());
        }
    }

    public void handleVerifyCode(ActionEvent actionEvent) {
        try {
            UserValidator.getInstance().validateEmail(emailField.getText());

            if (userService.verifyPasswordResetCode(emailField.getText(), codeField.getText())) {
                AlertHelper.successAlert("Code is valid!");

                emailField.setDisable(true);
                codeField.setDisable(true);
                sendResetCodeButton.setDisable(true);
                verifyCodeButton.setDisable(true);
                passwordField.setDisable(false);
                confirmButton.setDisable(false);
                passwordField.requestFocus();

            } else {
                AlertHelper.alert(Alert.AlertType.ERROR, "Invalid Code", "The code you entered is invalid. Please check it again and retry.");
            }
        } catch (PersistenceException e) {
            AlertHelper.persistenceExceptionAlert(e.getMessage());
        } catch (InvalidArgumentException e) {
            AlertHelper.invalidArgumentExceptionAlert(e.getMessage());
        }
    }

    public void handleConfirm(ActionEvent actionEvent) {
        try {
            UserValidator.getInstance().validateEmail(emailField.getText());
            UserValidator.getInstance().validatePassword(passwordField.getText());

            UserCredentials credentials = new UserCredentials(
                emailField.getText(),
                PasswordHelpers.hashOnClient(passwordField.getText())
            );
            User user = userService.resetPassword(credentials.getEmail(), codeField.getText(), credentials.getPassword());
            authenticationService.setStoredCredentials(credentials);
            authenticationService.setCachedUser(user);
            uiService.handleLoginCompleted();
        } catch (PersistenceException e) {
            AlertHelper.persistenceExceptionAlert(e.getMessage());
        } catch (InvalidArgumentException e) {
            AlertHelper.invalidArgumentExceptionAlert(e.getMessage());
        }
    }

}
