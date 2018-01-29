package at.ac.tuwien.sepm.fridget.client.controllers;

import at.ac.tuwien.sepm.fridget.client.services.ClientGroupService;
import at.ac.tuwien.sepm.fridget.client.util.AlertHelper;
import at.ac.tuwien.sepm.fridget.common.entities.GroupInvitation;
import at.ac.tuwien.sepm.fridget.common.exception.InvalidArgumentException;
import at.ac.tuwien.sepm.fridget.common.exception.PersistenceException;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

@Component
public class PendingInvitationsController implements Initializable {

    @FXML
    public ListView<GroupInvitation> listView_pendingInvitations;
    @Autowired
    ClientGroupService groupService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        updateInvitationsList();
    }

    private void updateInvitationsList() {
        try {
            List<GroupInvitation> groupInvitationList = groupService.getPendingInvitations(-1);
            if (groupInvitationList != null && !groupInvitationList.isEmpty()) {
                listView_pendingInvitations.getItems().clear();
                listView_pendingInvitations.setItems(FXCollections.observableArrayList(groupInvitationList));
                MenuItem acceptInvitation = new MenuItem("Accept Invitation");
                acceptInvitation.setOnAction((javafx.event.ActionEvent event) -> acceptInvitation());
                MenuItem rejectInvitation = new MenuItem("Reject Invitation");
                rejectInvitation.setOnAction((javafx.event.ActionEvent event) -> rejectInvitation());
                ContextMenu contextMenu = new ContextMenu();
                contextMenu.getItems().addAll(acceptInvitation, rejectInvitation);
                listView_pendingInvitations.contextMenuProperty().setValue(contextMenu);
            } else {
                listView_pendingInvitations.getItems().clear();
                AlertHelper.alert(Alert.AlertType.INFORMATION, "Invitations",
                    "No pending invitations found for this user");
            }
        } catch (PersistenceException e) {
            AlertHelper.persistenceExceptionAlert(e.getMessage());
        } catch (InvalidArgumentException e) {
            AlertHelper.invalidArgumentExceptionAlert(e.getMessage());
        }

    }

    private void acceptInvitation() {
        if (listView_pendingInvitations.getSelectionModel().getSelectedItem() == null) {
            AlertHelper.invalidArgumentExceptionAlert("No Invitation selected!");
        } else {
            try {
                groupService.acceptInvitation(listView_pendingInvitations.getSelectionModel()
                    .getSelectedItem().getId(), -1);
                AlertHelper.successAlert("Group successfully accepted!");
                updateInvitationsList();
            } catch (PersistenceException e) {
                AlertHelper.persistenceExceptionAlert(e.getMessage());
            } catch (InvalidArgumentException e) {
                AlertHelper.invalidArgumentExceptionAlert(e.getMessage());
            }

        }
    }

    private void rejectInvitation() {
        if (listView_pendingInvitations.getSelectionModel().getSelectedItem() == null) {
            AlertHelper.invalidArgumentExceptionAlert("No Invitation selected!");
        } else {
            try {
                groupService.rejectInvitation(listView_pendingInvitations.getSelectionModel()
                    .getSelectedItem().getId(), -1);
                AlertHelper.successAlert("Group successfully rejected!");
                updateInvitationsList();
            } catch (PersistenceException e) {
                AlertHelper.persistenceExceptionAlert(e.getMessage());
            } catch (InvalidArgumentException e) {
                AlertHelper.invalidArgumentExceptionAlert(e.getMessage());
            }
        }

    }
}
