package at.ac.tuwien.sepm.fridget.client.controllers;

import at.ac.tuwien.sepm.fridget.client.services.ClientBillService;
import at.ac.tuwien.sepm.fridget.client.services.ClientGroupService;
import at.ac.tuwien.sepm.fridget.client.services.ClientTagService;
import at.ac.tuwien.sepm.fridget.client.services.ClientUserService;
import at.ac.tuwien.sepm.fridget.client.springfx.SpringFxmlLoader;
import at.ac.tuwien.sepm.fridget.client.uimodels.BillUIModel;
import at.ac.tuwien.sepm.fridget.client.util.AlertHelper;
import at.ac.tuwien.sepm.fridget.common.entities.*;
import at.ac.tuwien.sepm.fridget.common.exception.InvalidArgumentException;
import at.ac.tuwien.sepm.fridget.common.exception.PersistenceException;
import at.ac.tuwien.sepm.fridget.common.util.Currency;
import at.ac.tuwien.sepm.fridget.common.util.ShareTechniqueId;
import at.ac.tuwien.sepm.fridget.common.util.StringMoneyConverter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * JavaFX Controller to show all bills of a group
 */
@Component
public class BillHistoryController implements Initializable {

    /**
     * How many bills should be displayed per page
     */
    private final static int ROWS_PER_PAGE = 16;
    @Autowired
    private ClientBillService billService;
    @Autowired
    private ClientGroupService groupService;
    @Autowired
    private ClientTagService tagService;
    @Autowired
    private ClientUserService userService;
    @Autowired
    private StringMoneyConverter stringMoneyConverter;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    SpringFxmlLoader springFxmlLoader;
    @Autowired
    private ApplicationController applicationController;

    @FXML
    private ChoiceBox<Group> choiceBox_groups;
    @FXML
    private DatePicker datePicker_from;
    @FXML
    private DatePicker datePicker_to;
    @FXML
    private TextField txtField_minAmount;
    @FXML
    private TextField txtField_maxAmount;
    @FXML
    private TextField txtField_description;
    @FXML
    private ComboBox<User> comboBox_user;
    @FXML
    private ComboBox<Tag> comboBox_tag;
    @FXML
    private RadioButton radioBtn_All;
    @FXML
    private RadioButton radioBtn_Valid;
    @FXML
    private RadioButton radioBtn_Deleted;
    @FXML
    private Button btn_search;
    @FXML
    private Button btn_reset;
    @FXML
    private TableView<BillUIModel> tbl_view_bills;
    @FXML
    private TableColumn<BillUIModel, Boolean> tbl_column_bill_deleted;
    @FXML
    private TableColumn<BillUIModel, String> tbl_column_bill_title;
    @FXML
    private TableColumn<BillUIModel, User> tbl_column_bill_payer;
    @FXML
    private TableColumn<BillUIModel, ShareTechniqueId> tbl_column_bill_share_technique;
    @FXML
    private TableColumn<BillUIModel, BigDecimal> tbl_column_bill_amount;
    @FXML
    private TableColumn<BillUIModel, Currency> tbl_column_bill_currency;
    @FXML
    private Pagination pagination;
    @FXML
    private TextArea txtArea_Description;
    @FXML
    private TextArea txtArea_Tag;
    @FXML
    private Label label_exchangeRate;
    @FXML
    private Label label_createdAt;
    @FXML
    private Label label_updatedAt;
    @FXML
    private Label lbl_total;
    @FXML
    private Label lbl_group_base_currency;
    /**
     * User interface objects for bills
     */
    private ObservableList<BillUIModel> billUIModels;

    private ToggleGroup showDeletedToggleGroup = new ToggleGroup();

    private SpringFxmlLoader.AnyWrapper<CreateBillController, AnchorPane> wrapper;

    private Scene scene;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        wrapper = springFxmlLoader.loadAndWrapAny("/fxml/createBill.fxml");
        billUIModels = FXCollections.observableArrayList();
        billUIModels.clear();

        tbl_view_bills.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> showBillDetails(newValue));

        MenuItem deleteBill = new MenuItem("Delete Bill(s)");
        deleteBill.setOnAction((javafx.event.ActionEvent event) -> handleDeleteBills());

        MenuItem undoDeleteBill = new MenuItem("Undo Deletion of Bill(s)");
        undoDeleteBill.setOnAction((javafx.event.ActionEvent event) -> handleUndoDeleteBills());

        MenuItem editBill = new MenuItem("Edit Bill");
        editBill.setOnAction((javafx.event.ActionEvent event) -> handleEditBill());

        ContextMenu contextMenu = new ContextMenu();
        contextMenu.getItems().addAll(deleteBill, undoDeleteBill, editBill);
        tbl_view_bills.contextMenuProperty().setValue(contextMenu);
        tbl_view_bills.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tbl_view_bills.setRowFactory(tableView -> {
            TableRow<BillUIModel> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    handleEditBill();
                }
            });
            return row;
        });

        try {
            List<Group> groups = groupService.getGroups(-1);
            choiceBox_groups.getItems().setAll(groups);
            if (!choiceBox_groups.getItems().isEmpty()) {
                choiceBox_groups.setValue(groups.get(0));
                BillQuery billQuery = new BillQuery();
                billQuery.setGroup(groups.get(0));
                comboBox_tag.getItems().setAll(tagService.getTagsForGroup(null, groups.get(0).getId()));
                comboBox_tag.getItems().add(null);
                comboBox_tag.setCellFactory(param -> new ListCell<Tag>() {
                    @Override
                    protected void updateItem(Tag tag, boolean empty) {
                        super.updateItem(tag, empty);
                        if (empty || tag == null)
                            setText("<None>");
                        else
                            setText(tag.toString());
                    }
                });
                comboBox_user.getItems().setAll(groupService.getUsersForGroup(groups.get(0).getId(), -1));
                comboBox_user.getItems().add(null);
                comboBox_user.setCellFactory(param -> new ListCell<User>() {
                    @Override
                    protected void updateItem(User user, boolean empty) {
                        super.updateItem(user, empty);
                        if (empty || user == null)
                            setText("<None>");
                        else
                            setText(user.toString());
                    }
                });
                setFilter();
            }
        } catch (PersistenceException e) {
            AlertHelper.persistenceExceptionAlert(e.getMessage());
        } catch (InvalidArgumentException e) {
            AlertHelper.invalidArgumentExceptionAlert(e.getMessage());
        }


        choiceBox_groups.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            try {
                comboBox_tag.getItems().setAll(tagService.getTagsForGroup(null, newValue.getId()));
                comboBox_user.getItems().setAll(groupService.getUsersForGroup(newValue.getId(), -1));
            } catch (PersistenceException e) {
                AlertHelper.persistenceExceptionAlert(e.getMessage());
            } catch (InvalidArgumentException e) {
                AlertHelper.invalidArgumentExceptionAlert(e.getMessage());
            }
            BillQuery billQuery = new BillQuery();
            billQuery.setGroup(newValue);
            showBillHistory(billQuery);
        });

        datePicker_from.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (datePicker_from.getValue() == null)
                return;
            if (datePicker_to.getValue() != null && datePicker_to.getValue().isBefore(datePicker_from.getValue())) {
                AlertHelper.alert(Alert.AlertType.WARNING, "Datetime", "Start date must not be after end date");
                datePicker_from.setValue(oldValue);
                return;
            }
            setFilter();
        });

        datePicker_to.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (datePicker_to.getValue() == null)
                return;
            if (datePicker_to.getValue() != null && datePicker_from.getValue() != null && datePicker_to.getValue().isBefore(datePicker_from.getValue())) {
                AlertHelper.alert(Alert.AlertType.WARNING, "Datetime", "End date must not be before start date");
                datePicker_to.setValue(oldValue);
                return;
            }
            setFilter();
        });

        txtField_minAmount.addEventFilter(KeyEvent.KEY_TYPED, (KeyEvent event) -> {
            if (!event.getCharacter().matches("[0-9.]"))
                event.consume();
        });

        txtField_maxAmount.addEventFilter(KeyEvent.KEY_TYPED, (KeyEvent event) -> {
            if (!event.getCharacter().matches("[0-9.]"))
                event.consume();
        });

        pagination.currentPageIndexProperty().addListener((observable, oldValue, newValue) -> {
            int fromIndex = newValue.intValue() * ROWS_PER_PAGE;
            int toIndex = Math.min(fromIndex + ROWS_PER_PAGE, billUIModels.size());
            tbl_view_bills.setItems(FXCollections.observableArrayList(billUIModels.subList(fromIndex, toIndex)));
        });

        tbl_column_bill_title.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
        tbl_column_bill_payer.setCellValueFactory(cellData -> cellData.getValue().payerProperty());
        tbl_column_bill_amount.setCellValueFactory(cellData -> cellData.getValue().amountProperty());
        tbl_column_bill_share_technique.setCellValueFactory(cellData -> cellData.getValue().shareTechniqueIdProperty());
        tbl_column_bill_currency.setCellValueFactory(cellData -> cellData.getValue().currencyProperty());
        tbl_column_bill_deleted.setCellValueFactory(cellData -> cellData.getValue().deletedProperty());

        lbl_group_base_currency.setText("");

        // show All/Valid/Deleted
        radioBtn_All.setToggleGroup(showDeletedToggleGroup);
        radioBtn_Valid.setToggleGroup(showDeletedToggleGroup);
        radioBtn_Deleted.setToggleGroup(showDeletedToggleGroup);
        showDeletedToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isSelected())
                setFilter();
        });
    }

    /**
     * Display bills for a group upon selection of a group from the choicebox
     *
     * @param billQuery the corresponding bill query for the selected group
     */
    private void showBillHistory(BillQuery billQuery) {
        if (billQuery == null)
            return;
        else if (billUIModels != null)
            billUIModels.clear();
        if (billQuery.getGroup() == null)
            return;

        try {
            for (Bill bill : billService.listAllBills(billQuery)) {
                BillUIModel billUIModel = modelMapper.map(bill, BillUIModel.class);
                billUIModels.add(billUIModel);
            }

            datePicker_from.setDisable(false);
            datePicker_to.setDisable(false);
            txtField_minAmount.setDisable(false);
            txtField_maxAmount.setDisable(false);
            txtField_description.setDisable(false);
            comboBox_user.setDisable(false);
            comboBox_tag.setDisable(false);
            radioBtn_All.setDisable(false);
            radioBtn_Deleted.setDisable(false);
            radioBtn_Valid.setDisable(false);
            btn_search.setDisable(false);

            pagination.setDisable(false);
            pagination.setPageCount(billUIModels.size() / ROWS_PER_PAGE + 1);
            tbl_view_bills.setItems(FXCollections.observableArrayList(billUIModels.subList(0, Math.min(ROWS_PER_PAGE, billUIModels.size()))));
            // Set total label currency symbol to group base currency
            lbl_group_base_currency.setText(Currency.fromId(billQuery.getGroup().getBaseCurrencyID()).getSymbol());
        } catch (PersistenceException e) {
            AlertHelper.persistenceExceptionAlert(e.getMessage());
        } catch (InvalidArgumentException e) {
            AlertHelper.invalidArgumentExceptionAlert(e.getMessage());
        }
    }

    @FXML
    private void setFilter() {
        BillQuery billQuery = new BillQuery();
        billQuery.setGroup(choiceBox_groups.getValue());
        billQuery.setText(txtField_description.getText());

        if (datePicker_from.getValue() != null)
            billQuery.setMinDate(datePicker_from.getValue().atStartOfDay());
        if (datePicker_to.getValue() != null)
            billQuery.setMaxDate(datePicker_to.getValue().atStartOfDay());

        if (txtField_minAmount.getText().matches("[0-9]*[.]?[0-9]"))
            billQuery.setMinAmount(BigDecimal.valueOf(Double.valueOf(txtField_minAmount.getText())));
        else if (txtField_minAmount.getText().trim().length() != 0) {
            AlertHelper.alert(Alert.AlertType.WARNING, "Invalid Argument", "Inserted min amount format wrong");
            return;
        }
        if (txtField_maxAmount.getText().matches("[0-9]*[.]?[0-9]"))
            billQuery.setMaxAmount(BigDecimal.valueOf(Double.valueOf(txtField_maxAmount.getText())));
        else if (txtField_maxAmount.getText().trim().length() != 0) {
            AlertHelper.alert(Alert.AlertType.WARNING, "Invalid Argument", "Inserted max amount format wrong");
            return;
        }

        billQuery.setDeleted(radioBtn_Deleted.isSelected());
        billQuery.setValid(radioBtn_Valid.isSelected());
        billQuery.setTag(comboBox_tag.getValue());
        billQuery.setPayer(comboBox_user.getValue());

        showBillHistory(billQuery);
        btn_reset.setDisable(false);
    }

    @FXML
    public void resetFilter() {
        datePicker_from.setValue(null);
        datePicker_to.setValue(null);
        comboBox_user.setValue(null);
        comboBox_tag.setValue(null);
        radioBtn_Valid.setSelected(true);
        txtField_maxAmount.clear();
        txtField_minAmount.clear();
        txtField_description.clear();
        btn_reset.setDisable(true);
        BillQuery billQuery = new BillQuery();
        billQuery.setGroup(choiceBox_groups.getValue());
        billQuery.setValid(true);
        showBillHistory(billQuery);
    }

    /**
     * Show detailed view of bill selected from table view
     *
     * @param billUIModel the bill ui model object for displaying and manipulating data
     */
    private void showBillDetails(BillUIModel billUIModel) {
        if (billUIModel == null) {
            txtArea_Description.setText(" - ");
            txtArea_Tag.setText(" - ");
            label_createdAt.setText(" - ");
            label_updatedAt.setText(" - ");
            label_exchangeRate.setText(" - ");
            lbl_total.setText(" - ");
            return;
        }

        txtArea_Description.setText(billUIModel.getDescription());
        txtArea_Tag.setText(billUIModel.getTag().getName());
        label_createdAt.setText(billUIModel.getCreatedAt().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT)));
        label_updatedAt.setText(billUIModel.getUpdatedAt().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT)));
        label_exchangeRate.setText(billUIModel.getExchangeRate().toString());
        lbl_total.setText(stringMoneyConverter.toString(billUIModel.getAmount().multiply(billUIModel.getExchangeRate())));
    }

    @FXML
    private void handleEditBill() {
        if (tbl_view_bills.getSelectionModel().getSelectedItems().size() > 1)
            AlertHelper.alert(Alert.AlertType.INFORMATION, "Edit bill", "Please select one bill only!");

        BillUIModel selectedBill = tbl_view_bills.getSelectionModel().getSelectedItem();

        Stage dialogStage = new Stage();
        dialogStage.setTitle("Edit Bill");

        CreateBillController controller = wrapper.getController();
        if (scene == null) {
            scene = new Scene(wrapper.getLoadedObject());
        } else {
            controller.closeStage();
            scene.setRoot(wrapper.getLoadedObject());
        }

        controller.setEditMode(true);
        controller.setBill(selectedBill);
        controller.setDialogStage(dialogStage);
        dialogStage.setScene(scene);
        dialogStage.setOnCloseRequest(windowEvent -> {
            applicationController.triggerMainStageEnabled();
            controller.closeStage();
        });
        applicationController.triggerMainStageEnabled();
        dialogStage.showAndWait();

        //refreshTable view
        BillQuery billQuery = new BillQuery();
        billQuery.setGroup(choiceBox_groups.getValue());
        setFilter();
    }

    @FXML
    private void handleDeleteBills() {
        List<BillUIModel> selectedProducts = tbl_view_bills.getSelectionModel().getSelectedItems();

        if (!selectedProducts.isEmpty()) {
            Optional<ButtonType> buttonType = AlertHelper.alert(Alert.AlertType.CONFIRMATION, "Delete Bill(s)", "Do you really want to delete the selected bill(s)?");

            if (buttonType.get().equals(ButtonType.OK)) {
                selectedProducts.forEach(bill -> {
                    try {
                        billService.deleteBill(bill.getId());
                        bill.setDeleted(true);
                    } catch (InvalidArgumentException e) {
                        AlertHelper.invalidArgumentExceptionAlert(e.getMessage());
                    } catch (PersistenceException e) {
                        AlertHelper.persistenceExceptionAlert(e.getMessage());
                    }
                });
                tbl_view_bills.refresh();
            }
        } else {
            AlertHelper.alert(Alert.AlertType.INFORMATION, "Nothing selected", "No bill(s) selected");
        }
    }

    @FXML
    private void handleUndoDeleteBills() {
        List<BillUIModel> selectedProducts = tbl_view_bills.getSelectionModel().getSelectedItems();

        if (!selectedProducts.isEmpty()) {
            Optional<ButtonType> buttonType = AlertHelper.alert(Alert.AlertType.CONFIRMATION, "Undo Delete of Bill(s)", "Do you really want to undo the deletion of the selected bill(s)?");

            if (buttonType.get().equals(ButtonType.OK)) {
                selectedProducts.forEach(bill -> {
                    try {
                        billService.undoDeleteBill(bill.getId());
                        bill.setDeleted(false);
                    } catch (InvalidArgumentException e) {
                        AlertHelper.invalidArgumentExceptionAlert(e.getMessage());
                    } catch (PersistenceException e) {
                        AlertHelper.persistenceExceptionAlert(e.getMessage());
                    }
                });
                tbl_view_bills.refresh();
            }
        } else {
            AlertHelper.alert(Alert.AlertType.INFORMATION, "Nothing selected", "No bill(s) selected");
        }
    }

    @Bean
    public SpringFxmlLoader.AnyWrapper<CreateBillController, AnchorPane> getWrapper() {
        return this.wrapper;
    }

    @Bean
    public Scene getScene() {
        return scene;
    }
}
