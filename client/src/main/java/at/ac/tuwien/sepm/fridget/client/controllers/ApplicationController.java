package at.ac.tuwien.sepm.fridget.client.controllers;

import at.ac.tuwien.sepm.fridget.client.services.AuthenticationService;
import at.ac.tuwien.sepm.fridget.client.services.UIService;
import at.ac.tuwien.sepm.fridget.client.springfx.SpringFxmlLoader;
import at.ac.tuwien.sepm.fridget.client.uimodels.BillUIModel;
import at.ac.tuwien.sepm.fridget.client.util.AlertHelper;
import at.ac.tuwien.sepm.fridget.client.util.StringUtil;
import at.ac.tuwien.sepm.fridget.common.entities.*;
import at.ac.tuwien.sepm.fridget.common.exception.InvalidArgumentException;
import at.ac.tuwien.sepm.fridget.common.exception.PersistenceException;
import at.ac.tuwien.sepm.fridget.common.services.BillService;
import at.ac.tuwien.sepm.fridget.common.services.EventService;
import at.ac.tuwien.sepm.fridget.common.services.GroupService;
import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

@Component
public class ApplicationController implements Initializable {

    @FXML
    public Button button_User;
    @Autowired
    SpringFxmlLoader springFxmlLoader;
    @Autowired
    AuthenticationService authenticationService;
    @Autowired
    UIService uiService;

    @Autowired
    EventService eventService;

    @Autowired
    GroupService groupService;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    BillService billService;

    @Autowired
    CreateBillController createBillController;

    @Autowired
    BillHistoryController billHistoryController;

    /**
     * The pane in which all content-related UI operations take place
     */
    @FXML
    Pane pane_display;

    /**
     * Veil to disable actions on the main stage while having a second stage, e.g. the edit bill dialog open
     */
    @FXML
    private Region veil;

    /**
     * List view containing several events that happened in the past e.g. someone created a bill, group invitations, etc.
     */
    @FXML
    private ListView<Event> list_view_log;

    @FXML
    private AnchorPane anchor_pane;

    @FXML
    public Label label_Username;

    @FXML
    public void handleCreateBill(ActionEvent actionEvent) {
        // If user is not in any group, inform and return
        try {
            if (groupService.getGroups(authenticationService.getUser().getId()).size() == 0) {
                AlertHelper.alert(Alert.AlertType.INFORMATION, "No groups yet", "You haven't joined a " +
                    "group yet. Please create a group first in order to create bills.");
                return;
            }
            // Update events
            eventService.checkForUpdates(authenticationService.getUser());
        } catch (PersistenceException e) {
            AlertHelper.persistenceExceptionAlert(e.getMessage());
        } catch (InvalidArgumentException e) {
            AlertHelper.invalidArgumentExceptionAlert(e.getMessage());
        }

        pane_display.getChildren().clear();
        pane_display.getChildren().add(new Scene(springFxmlLoader.load("/fxml/createBill.fxml")).getRoot());
    }

    @FXML
    public void handleShowBills(ActionEvent actionEvent) {
        checkForEventUpdates();
        pane_display.getChildren().clear();
        pane_display.getChildren().add(new Scene(springFxmlLoader.load("/fxml/billHistory.fxml")).getRoot());
    }

    @FXML
    public void handleCreateGroup(ActionEvent actionEvent) {
        checkForEventUpdates();
        pane_display.getChildren().clear();
        pane_display.getChildren().add(new Scene(springFxmlLoader.load("/fxml/createGroup.fxml")).getRoot());
    }

    @FXML
    public void handleEditUser(ActionEvent actionEvent) {
        pane_display.getChildren().clear();
        pane_display.getChildren().add(new Scene(springFxmlLoader.load("/fxml/editUser.fxml")).getRoot());
    }

    @FXML
    public void handleListAllGroups(ActionEvent actionEvent) {
        checkForEventUpdates();
        pane_display.getChildren().clear();
        pane_display.getChildren().add(new Scene(springFxmlLoader.load("/fxml/group.fxml")).getRoot());
    }

    @FXML
    public void handleLogout(ActionEvent actionEvent) {
        pane_display.getChildren().clear();
        authenticationService.logout();
        uiService.handleLogout();
    }

    public void handlePendingInvitations(ActionEvent actionEvent) {
        pane_display.getChildren().clear();
        pane_display.getChildren().add(new Scene(springFxmlLoader.load("/fxml/pendingInvitations.fxml")).getRoot());

    }

    public void handleUserUpdated(User user) {
        button_User.setText("Hello, " + user.getName());
    }

    /**
     * Setup notification log
     *
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        button_User.setText("Hello, " + authenticationService.getUser().getName());
        pane_display.getChildren().clear();
        pane_display.getChildren().add(new Scene(springFxmlLoader.load("/fxml/splashscreen.fxml")).getRoot());

        // Initialize veil
        veil.minWidthProperty().bindBidirectional(anchor_pane.minWidthProperty());
        veil.minHeightProperty().bindBidirectional(anchor_pane.minHeightProperty());
        veil.prefWidthProperty().bindBidirectional(anchor_pane.prefWidthProperty());
        veil.prefHeightProperty().bindBidirectional(anchor_pane.prefHeightProperty());
        veil.setVisible(false);

        // Set items on list view
        // Get pending invitations and create "events" for them, i.e. use the event logic to display the group notifications
        // and the actual events in the same list view
        try {
            List<Event> events = new ArrayList<>();
            List<GroupInvitation> pendingInvitations = groupService.getPendingInvitations(authenticationService.getUser().getId());
            pendingInvitations.forEach(groupInvitation -> {
                try {
                    events.add(new Event(EventType.PENDING_INVITATION,
                        false,
                        LocalDateTime.now(),
                        groupInvitation.getId() + "",// As interim solution
                        authenticationService.getUser(),// Not formally correct, acts as a dummy
                        authenticationService.getUser(),
                        null,
                        null,
                        groupService.getGroup(groupInvitation.getGroup().getId())));// Not normally valid, here it has to be the inviter's group
                } catch (PersistenceException e) {
                    AlertHelper.persistenceExceptionAlert(e.getMessage());
                } catch (InvalidArgumentException e) {
                    AlertHelper.invalidArgumentExceptionAlert(e.getMessage());
                }
            });
            events.addAll(eventService.fetchRecentEvents(null, -1));
            list_view_log.setItems(FXCollections.observableArrayList(events));
        } catch (PersistenceException e) {
            AlertHelper.persistenceExceptionAlert(e.getMessage());
        } catch (InvalidArgumentException e) {
            AlertHelper.invalidArgumentExceptionAlert(e.getMessage());
        }


        list_view_log.setCellFactory(param -> new EventListViewCell() {

            @Override
            protected void updateItem(Event item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setGraphic(null);
                    setText(null);
                } else {
                    if (item.getType() == null)
                        setText("Invalid event type.");

                    GridPane pane = new GridPane();
                    GridPane buttonPane = new GridPane();
                    pane.add(content, 0, 0);
                    buttonPane.add(acceptInvitation, 0, 0);
                    buttonPane.add(rejectInvitation, 0, 0);
                    pane.add(buttonPane, 0, 1);

                    pane.setMinWidth(list_view_log.getPrefWidth() - 35);
                    pane.setMaxWidth(list_view_log.getPrefWidth() - 35);
                    if (item.getType().equals(EventType.PENDING_INVITATION)) {
                        pane.setPrefHeight(200);
                        content.setPrefHeight(150);
                    } else {
                        buttonPane.setMaxHeight(0);
                        buttonPane.setVisible(false);
                        buttonPane.setManaged(false);
                        pane.setPrefHeight(60);
                        setPrefHeight(60);
                        setAlignment(Pos.CENTER);
                    }

                    content.setMinWidth(list_view_log.getPrefWidth() - 35);
                    content.setMaxWidth(list_view_log.getPrefWidth() - 35);

                    // Handle all event types
                    Text text = new Text("");
                    switch (item.getType()) {
                        case INVITATION_SENT:
                            content.setText(item.getSource().getName() + " invited " + item.getTarget().getName() + " to group "
                                + item.getGroup().getName());
                            break;
                        case INVITATION_ACCEPTED:
                            content.setText(item.getTarget().getName() + " accepted invitation to group " + item.getGroup().getName());

                            break;
                        case MEMBER_LEFT:
                            content.setText(item.getTarget().getName() + " left group " + item.getGroup());
                            break;
                        case MODIFY_BILL:
                            content.setText(item.getSource().getName() + " changed the following on bill " +
                                item.getBillAfter().getTitle() + ": " + item.getDescription());
                            break;
                        case CREATE_BILL:
                            content.setText(item.getDescription());
                            break;
                        case PENDING_INVITATION:
                            // If you are the recipient of the invitation
                            if (authenticationService.getUser().equals(item.getTarget())) {
                                content.setText("You were invited to join the group "
                                    + item.getGroup().getName() + ". \nDo you want to join?");
                                acceptInvitation.setOnAction(event -> {
                                    try {
                                        groupService.acceptInvitation(Integer.parseInt(item.getDescription()),
                                            item.getTarget().getId());
                                        AlertHelper.successAlert("You successfully joined the group " + item.getGroup().getName());
                                        getList_view_log().getItems().remove(item);
                                    } catch (PersistenceException e) {
                                        AlertHelper.persistenceExceptionAlert(e.getMessage());
                                    } catch (InvalidArgumentException e) {
                                        AlertHelper.invalidArgumentExceptionAlert(e.getMessage());
                                    }
                                    setVisible(false);
                                    setManaged(false);
                                });
                                rejectInvitation.setOnAction(event -> {
                                    try {
                                        groupService.rejectInvitation(Integer.parseInt(item.getDescription()),
                                            item.getTarget().getId());
                                    } catch (PersistenceException e) {
                                        AlertHelper.persistenceExceptionAlert(e.getMessage());
                                    } catch (InvalidArgumentException e) {
                                        AlertHelper.invalidArgumentExceptionAlert(e.getMessage());
                                    }
                                    setVisible(false);
                                    setManaged(false);
                                });
                            }
                            break;
                        default:
                            content.setText(null);
                            break;
                    }
                    // Limit text size
                    content.setWrapText(true);
                    text.setWrappingWidth(list_view_log.getPrefWidth() - 35);
                    text.setText(StringUtil.truncateWithEllipse(text.getText(), 80));

                    // Set tooltip if item has description
                    if (item.getDescription() != null
                        && !item.getDescription().isEmpty()
                        && !item.getType().equals(EventType.PENDING_INVITATION))
                        setTooltip(new Tooltip(item.getDescription()));

                    // Enable double clicking for bill modification events
                    if (item.getType().equals(EventType.MODIFY_BILL)
                        || item.getType().equals(EventType.CREATE_BILL)) {
                        this.setOnMouseClicked(event -> {
                            if (event.getClickCount() == 2) {
                                try {
                                    Bill bill = billService.findBillById(item.getBillAfter().getId());
                                    if (bill.isDeleted()) return;
                                    BillUIModel selectedBill = modelMapper.map(
                                        bill,
                                        BillUIModel.class);
                                    // Map tag if exists
                                    if (bill.getTag() != null)
                                        selectedBill.setTag(bill.getTag());

                                    Stage dialogStage = new Stage();
                                    dialogStage.setTitle("Edit Bill");

                                    if (billHistoryController.getWrapper() == null
                                        || billHistoryController.getWrapper().getLoadedObject() == null
                                        || billHistoryController.getScene() == null) {
                                        dialogStage.setScene(new Scene(springFxmlLoader.load("/fxml/createBill.fxml")));
                                    } else {
                                        createBillController.closeStage();

                                        billHistoryController.getScene().setRoot((billHistoryController.getWrapper().getLoadedObject()));
                                        dialogStage.setScene(billHistoryController.getScene());
                                    }
                                    createBillController.setEditMode(true);
                                    createBillController.setBill(selectedBill);
                                    createBillController.setDialogStage(dialogStage);

                                    // Disable user interactions on the main stage
                                    triggerMainStageEnabled();
                                    dialogStage.setOnCloseRequest(windowEvent -> triggerMainStageEnabled());
                                    dialogStage.showAndWait();
                                } catch (PersistenceException e) {
                                    AlertHelper.persistenceExceptionAlert(e.getMessage());
                                } catch (InvalidArgumentException e) {
                                    AlertHelper.invalidArgumentExceptionAlert(e.getMessage());
                                }
                            }
                        });
                    }
                    // Set content
                    setGraphic(pane);
                }
            }
        });
        list_view_log.setPlaceholder(new Label("No events yet"));

    }

    @Bean
    public ListView<Event> getList_view_log() {
        return this.list_view_log;
    }

    @Bean
    public Pane getPane_display() {
        return this.pane_display;
    }

    private FadeTransition fade = new FadeTransition(Duration.millis(350));

    /**
     * Show/Hide veil that prevents user interaction while a second stage is open
     */
    public void triggerMainStageEnabled() {
        fade.setNode(veil);
        fade.setCycleCount(1);
        fade.setAutoReverse(false);
        fade.setOnFinished(null);
        if (veil.isVisible()) {
            fade.setFromValue(1.0);
            fade.setToValue(0.0);
            fade.setOnFinished(event -> veil.setVisible(false));
        } else {
            veil.setVisible(true);
            fade.setFromValue(0.0);
            fade.setToValue(1.0);
        }
        fade.playFromStart();
    }

    /**
     * Fetch new events on scene change
     */
    private void checkForEventUpdates() {
        try {
            // Update events
            eventService.checkForUpdates(authenticationService.getUser());
        } catch (PersistenceException e) {
            AlertHelper.persistenceExceptionAlert(e.getMessage());
        } catch (InvalidArgumentException e) {
            AlertHelper.invalidArgumentExceptionAlert(e.getMessage());
        }
    }

    private class EventListViewCell extends ListCell<Event> {
        Label content;
        Button acceptInvitation;
        Button rejectInvitation;

        EventListViewCell() {
            super();
            setPrefHeight(100);

            // Setup elements
            acceptInvitation = new Button();
            acceptInvitation.setText("✓");
            rejectInvitation = new Button("❌");
            rejectInvitation.setLayoutX(acceptInvitation.getPrefWidth());
            rejectInvitation.setTranslateX(35);
            content = new Label();
        }
    }
}
