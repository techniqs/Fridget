package at.ac.tuwien.sepm.fridget.client.controllers;

import at.ac.tuwien.sepm.fridget.client.services.AuthenticationService;
import at.ac.tuwien.sepm.fridget.client.services.ClientUserService;
import at.ac.tuwien.sepm.fridget.client.services.UIService;
import at.ac.tuwien.sepm.fridget.client.util.AlertHelper;
import at.ac.tuwien.sepm.fridget.common.entities.User;
import at.ac.tuwien.sepm.fridget.common.exception.InvalidArgumentException;
import at.ac.tuwien.sepm.fridget.common.exception.PersistenceException;
import at.ac.tuwien.sepm.fridget.common.util.UserValidator;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
public class EditUserController implements Initializable {

    @Autowired
    private ClientUserService userService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private UIService uiService;

    @FXML
    public TextField txt_email;

    @FXML
    public TextField txt_userName;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        txt_email.setText(authenticationService.getUser().getEmail());
        txt_userName.setText(authenticationService.getUser().getName());
    }

    @FXML
    public void handleEdit() {
        try {
            UserValidator.getInstance().validateName(txt_userName.getText());
            User user = new User();
            user.setId(authenticationService.getUser().getId());
            user.setName(txt_userName.getText());
            User newUser = userService.editUser(user);
            authenticationService.setCachedUser(newUser);
            uiService.handleUserUpdated(newUser);
            AlertHelper.successAlert("User successfully edited");
        } catch (PersistenceException e) {
            AlertHelper.persistenceExceptionAlert(e.getMessage());
        } catch (InvalidArgumentException e) {
            AlertHelper.invalidArgumentExceptionAlert(e.getMessage());
        }
    }
}
