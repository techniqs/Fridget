package at.ac.tuwien.sepm.fridget.client.controllers;

import at.ac.tuwien.sepm.fridget.client.services.ClientGroupService;
import at.ac.tuwien.sepm.fridget.client.services.ClientTagService;
import at.ac.tuwien.sepm.fridget.client.services.ClientTransactionService;
import at.ac.tuwien.sepm.fridget.client.services.IAuthenticationService;
import at.ac.tuwien.sepm.fridget.client.springfx.SpringFxmlLoader;
import at.ac.tuwien.sepm.fridget.client.uimodels.GroupBalanceUiModel;
import at.ac.tuwien.sepm.fridget.client.util.AlertHelper;
import at.ac.tuwien.sepm.fridget.client.util.GroupUiValidationUtil;
import at.ac.tuwien.sepm.fridget.common.entities.Group;
import at.ac.tuwien.sepm.fridget.common.entities.Tag;
import at.ac.tuwien.sepm.fridget.common.entities.User;
import at.ac.tuwien.sepm.fridget.common.exception.BalanceNotZeroException;
import at.ac.tuwien.sepm.fridget.common.exception.InvalidArgumentException;
import at.ac.tuwien.sepm.fridget.common.exception.PersistenceException;
import at.ac.tuwien.sepm.fridget.common.util.StringMoneyConverter;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class GroupController implements Initializable {


    @FXML
    public TableView<GroupBalanceUiModel> membersTableView;

    @FXML
    public ListView<Tag> tagListView;
    @FXML
    public Label balanceLabel;
    @FXML
    public Label group_name;
    @FXML
    public ChoiceBox<Group> dropDownAllGroups;
    @FXML
    public TextField txt_userInvite;
    @FXML
    public TextField txt_newGroupName;
    @FXML
    public TextField txt_addTag;
    @FXML
    public Label inviteUserLabel;
    @FXML
    public Button inviteButton;
    @FXML
    public Button editButton;
    @FXML
    public Button leaveGroupButton;
    @FXML
    public Label editGroupNameLabel;
    @FXML
    public Label txt_balance;
    @FXML
    public Label addTagLabel;
    @FXML
    public Button addTagButton;
    @FXML
    public TableColumn<GroupBalanceUiModel, String> list_UserName;
    @FXML
    public TableColumn<GroupBalanceUiModel, BigDecimal> list_Balance;
    @FXML
    public Label label_IBalances;
    @FXML
    public Label label_Tags;

    @Autowired
    private ClientGroupService groupService;
    @Autowired
    private ClientTagService tagService;
    @Autowired
    private ClientTransactionService transactionService;
    @Autowired
    private IAuthenticationService authenticationService;
    @Autowired
    SpringFxmlLoader springFxmlLoader;

    private Scene scene;


    private List<Group> groupList;
    private List<Tag> tagList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        updateGroupList(-1);

        DecimalFormat df = new DecimalFormat("#,##0.00");

        list_UserName.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getUser().getName()));
        list_Balance.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getBalance()));
        list_Balance.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<>() {
            @Override
            public String toString(BigDecimal value) {
                int diff = value.compareTo(BigDecimal.ZERO);
                if (diff < 0) {
                    return "You owe them " + df.format(value.negate());
                } else if (diff > 0) {
                    return "They owe you " + df.format(value);
                } else {
                    return "Settled up";
                }
            }

            @Override
            public BigDecimal fromString(String string) {
                throw new UnsupportedOperationException();
            }
        }));
        MenuItem compensateDebt = new MenuItem("Compensate Debt");
        compensateDebt.setOnAction((javafx.event.ActionEvent event) -> handleCompensateDebt());
        ContextMenu contextMenu = new ContextMenu();
        contextMenu.getItems().addAll(compensateDebt);
        membersTableView.contextMenuProperty().setValue(contextMenu);

    }

    private void updateTagList() {
        getTagList();
        tagListView.setItems(FXCollections.observableArrayList(tagList));
    }

    private void getTagList() {
        try {
            tagList = tagService.getTagsForGroup(null, getCurrentGroupId());
        } catch (PersistenceException e) {
            AlertHelper.persistenceExceptionAlert(e.getMessage());
        } catch (InvalidArgumentException e) {
            AlertHelper.invalidArgumentExceptionAlert(e.getMessage());
        }
    }

    private int getCurrentGroupId() {
        return dropDownAllGroups.getSelectionModel().getSelectedItem().getId();
    }

    private void getGroupList() {
        try {
            groupList = (groupService.getGroups(-1));
        } catch (PersistenceException e) {
            AlertHelper.persistenceExceptionAlert(e.getMessage());
        }
    }

    /**
     * Needed for setting specific data for groups
     */
    private void setSelectedGroup() {
        setVisible(true);

        try {
            membersTableView.getItems().clear();

            int groupId = dropDownAllGroups.getSelectionModel().getSelectedItem().getId();

            Map<Integer, BigDecimal> balanceToMembers = transactionService.getBalanceToMembers(groupId, -1);

            List<GroupBalanceUiModel> elements = groupService.getUsersForGroup(groupId, -1).stream()
                .filter(u -> u.getId() != authenticationService.getUser().getId())
                .sorted(Comparator.comparing(User::getName))
                .map(u -> new GroupBalanceUiModel(u, balanceToMembers.getOrDefault(u.getId(), BigDecimal.ZERO)))
                .collect(Collectors.toCollection(ArrayList::new));

            membersTableView.getItems().setAll(elements);
            txt_balance.setText(transactionService.getBalance(groupId, -1).toString());

        } catch (PersistenceException e) {
            AlertHelper.persistenceExceptionAlert(e.getMessage());
        } catch (InvalidArgumentException e) {
            AlertHelper.invalidArgumentExceptionAlert(e.getMessage());
        }

    }

    private void updateGroupList(int groupIndex) {
        dropDownAllGroups.getItems().clear();
        getGroupList();
        if (groupList != null && !groupList.isEmpty()) {

            dropDownAllGroups.setItems(FXCollections.observableArrayList(groupList));

            dropDownAllGroups.getSelectionModel().selectedItemProperty().addListener((observable, oldSelection, newSelection) -> {
                if (newSelection != null) {

                    setSelectedGroup();
                    updateTagList();
                }
            });
            if (groupIndex >= 0) {
                dropDownAllGroups.getSelectionModel().select(groupIndex);
            } else {
                dropDownAllGroups.getSelectionModel().selectFirst();
            }

        } else {
            setVisible(false);
        }
    }

    private void setVisible(boolean value) {
        inviteButton.setVisible(value);
        txt_userInvite.setVisible(value);
        inviteUserLabel.setVisible(value);
        editButton.setVisible(value);
        txt_newGroupName.setVisible(value);
        leaveGroupButton.setVisible(value);
        balanceLabel.setVisible(value);
        editGroupNameLabel.setVisible(value);
        txt_balance.setVisible(value);
        membersTableView.setVisible(value);
        addTagLabel.setVisible(value);
        txt_addTag.setVisible(value);
        addTagButton.setVisible(value);
        tagListView.setVisible(value);
        label_Tags.setVisible(value);
        label_IBalances.setVisible(value);
    }

    @FXML
    public void handleInviteButton() {
        try {
            groupService.inviteUserToGroup(dropDownAllGroups.getSelectionModel().getSelectedItem().getId(), -1, txt_userInvite.getText());
            AlertHelper.successAlert(txt_userInvite.getText() + " successfully invited!");
        } catch (PersistenceException e) {
            AlertHelper.persistenceExceptionAlert(e.getMessage());
        } catch (InvalidArgumentException e) {
            AlertHelper.invalidArgumentExceptionAlert(e.getMessage());
        }
    }

    @FXML
    public void handleEditButton() {
        try {
            Group group = new Group(dropDownAllGroups.getSelectionModel().getSelectedItem().getId(), txt_newGroupName.getText(), dropDownAllGroups.getSelectionModel().getSelectedItem().getBaseCurrencyID());
            if (!GroupUiValidationUtil.uiValidation(group)) {
                return;
            }
            dropDownAllGroups.getSelectionModel().getSelectedItem().setName(txt_newGroupName.getText());
            groupService.editGroup(dropDownAllGroups.getSelectionModel().getSelectedItem(), -1);
            updateGroupList(dropDownAllGroups.getSelectionModel().getSelectedIndex());

            AlertHelper.alert(Alert.AlertType.INFORMATION, "Edit Successful!", "Name of the Group changed to: " + txt_newGroupName.getText());
        } catch (PersistenceException e) {
            AlertHelper.persistenceExceptionAlert(e.getMessage());
        } catch (InvalidArgumentException e) {
            AlertHelper.invalidArgumentExceptionAlert(e.getMessage());
        }
    }

    @FXML
    public void handleLeaveGroupButton() {
        try {
            Optional<ButtonType> result = AlertHelper.alert(Alert.AlertType.CONFIRMATION, "Leaving Group", "Are you sure you want to leave the group: " + dropDownAllGroups.getSelectionModel().getSelectedItem().getName());
            if (result.get() == ButtonType.OK) {
                //User wants to delete group
                Group group = new Group(dropDownAllGroups.getSelectionModel().getSelectedItem().getId());
                groupService.leaveGroup(group.getId(), -1);
                updateGroupList(-1);
            }
        } catch (PersistenceException e) {
            AlertHelper.persistenceExceptionAlert(e.getMessage());
        } catch (BalanceNotZeroException e) {
            AlertHelper.alert(Alert.AlertType.ERROR, "Leave Group Failure", e.getMessage());
        } catch (InvalidArgumentException e) {
            AlertHelper.invalidArgumentExceptionAlert(e.getMessage());
        }
    }

    @FXML
    public void handleAddTag() {
        try {
            Tag tag = new Tag(txt_addTag.getText(), getCurrentGroupId());
            validateTag(tag);
            tagService.createTag(null, tag);
            updateTagList();
        } catch (PersistenceException e) {
            AlertHelper.persistenceExceptionAlert(e.getMessage());
        } catch (InvalidArgumentException e) {
            AlertHelper.invalidArgumentExceptionAlert(e.getMessage());
        }
    }

    @FXML
    public void handleRequestCompensation(){
        try {
            groupService.sendDebtInfoMail(getCurrentGroupId(), null);
            AlertHelper.successAlert("Successfully sent messages!");
        } catch (InvalidArgumentException e) {
            AlertHelper.invalidArgumentExceptionAlert(e.getMessage());
        } catch (PersistenceException e) {
            AlertHelper.persistenceExceptionAlert(e.getMessage());
        }
    }

    private void handleCompensateDebt() {

        if (membersTableView.getSelectionModel().getSelectedItem() == null) {
            AlertHelper.invalidArgumentExceptionAlert("No Member selected!");
        } else if (membersTableView.getSelectionModel().getSelectedItem().getBalance().compareTo(new BigDecimal(0)) == 0) {
            AlertHelper.invalidArgumentExceptionAlert("Everything settled up with this Member!");
        } else {
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Settle Amount");
            SpringFxmlLoader.AnyWrapper<DialogController, AnchorPane> wrapper = springFxmlLoader.loadAndWrapAny("/fxml/dialogToSettleUp.fxml");
            DialogController controller = wrapper.getController();
            if (scene == null)
                scene = new Scene(wrapper.getLoadedObject());
            else {
                controller.handleCancelButton();
                scene = new Scene(wrapper.getLoadedObject());
            }

            controller.setMemberToSettle(membersTableView.getSelectionModel().getSelectedItem());
            controller.setFields(dropDownAllGroups.getSelectionModel().getSelectedItem());
            controller.setDialogStage(dialogStage);
            dialogStage.setScene(scene);
            dialogStage.showAndWait();
            initialize(null, null);

        }
    }

    private void validateTag(Tag tag) throws InvalidArgumentException {
        if (tag.getName() == null || tag.getName().isEmpty()) throw new InvalidArgumentException("Tag needs a name");
    }

}
