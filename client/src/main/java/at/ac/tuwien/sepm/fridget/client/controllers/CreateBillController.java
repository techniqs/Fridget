package at.ac.tuwien.sepm.fridget.client.controllers;

import at.ac.tuwien.sepm.fridget.client.services.ClientBillService;
import at.ac.tuwien.sepm.fridget.client.services.UIService;
import at.ac.tuwien.sepm.fridget.client.uimodels.BillShareUIModel;
import at.ac.tuwien.sepm.fridget.client.uimodels.BillUIModel;
import at.ac.tuwien.sepm.fridget.client.util.AlertHelper;
import at.ac.tuwien.sepm.fridget.common.entities.*;
import at.ac.tuwien.sepm.fridget.common.exception.InvalidArgumentException;
import at.ac.tuwien.sepm.fridget.common.exception.PersistenceException;
import at.ac.tuwien.sepm.fridget.common.services.EventService;
import at.ac.tuwien.sepm.fridget.common.services.GroupService;
import at.ac.tuwien.sepm.fridget.common.services.OCRService;
import at.ac.tuwien.sepm.fridget.common.services.TagService;
import at.ac.tuwien.sepm.fridget.common.util.Currency;
import at.ac.tuwien.sepm.fridget.common.util.ShareTechniqueId;
import at.ac.tuwien.sepm.fridget.common.util.StringCurrencyConverter;
import at.ac.tuwien.sepm.fridget.common.util.StringMoneyConverter;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * JavaFX Controller for creating Bills
 */
@Component
public class CreateBillController implements Initializable {

    /**
     * List of bill shares, "selected" is SET TO false if a user is not to be included in the bill
     * See {@link #generateEmptyBillShares(int groupId, List)}
     * See {@link BillShareUIModel}
     */
    private final ObjectProperty<ObservableList<BillShareUIModel>> billShareUIModels = new SimpleObjectProperty<>();
    @Autowired
    private ClientBillService billService;
    @Autowired
    private StringMoneyConverter stringMoneyConverter;
    @Autowired
    private StringCurrencyConverter stringCurrencyConverter;
    @Autowired
    private GroupService groupService;
    @Autowired
    private TagService tagService;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private UIService uiService;
    @Autowired
    private OCRService ocrService;
    // UI element for binding UI data
    private BillUIModel billUIModel;
    @Autowired
    private EventService eventService;
    @Autowired
    private ApplicationController applicationController;

    //mode to edit bill
    private boolean isEditMode;
    //for the case it is opened in a dialog (e.g. edit)
    private Stage stage;
    private ObservableList<Group> availableGroups;
    private ObservableList<Tag> availableTags = FXCollections.observableArrayList();

    /* FXML */
    @FXML
    private TextField txt_title;
    @FXML
    private Label lbl_title;
    @FXML
    private Button btn;
    @FXML
    private TableView<BillShareUIModel> tbl_view_bill_shares;
    @FXML
    private TableColumn<BillShareUIModel, CheckBox> tbl_column_bill_shares_select;
    @FXML
    private TableColumn<BillShareUIModel, String> tbl_column_bill_shares_name;
    @FXML
    private TableColumn<BillShareUIModel, String> tbl_column_bill_shares_input;
    @FXML
    private TextField txt_amount;
    @FXML
    private RadioButton rb_even, rb_percent, rb_absolute;
    @FXML
    private TextArea txt_description;
    @FXML
    private ComboBox<Group> cbb_group;
    @FXML
    private ComboBox<Tag> cbb_tag;
    @FXML
    private ComboBox<Currency> cbb_currency;
    @FXML
    private TextField txt_exchange_rate;
    @FXML
    private Label lbl_base_currency;
    @FXML
    private Label lbl_base_currency_symbol;
    @FXML
    private Label lbl_group;

    /* Custom properties */
    private StringProperty tbl_column_bill_shares_text_property = new SimpleStringProperty();

    // Toggle groups (for radio buttons)
    private ToggleGroup shareToggleGroup = new ToggleGroup();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize ui model
        billUIModel = new BillUIModel();
        isEditMode = false;

        // GROUPS
        // Fetch groups available for user
        try {
            availableGroups = FXCollections.observableArrayList(groupService.getGroups(-1));

            // Generate bill share placeholders
            generateEmptyBillShares(groupService.getGroups(-1).get(0).getId(), null);
        } catch (PersistenceException e) {
            AlertHelper.persistenceExceptionAlert(e.getMessage());
            return;
        } catch (InvalidArgumentException e) {
            AlertHelper.invalidArgumentExceptionAlert(e.getMessage());
            return;
        }

        // Group combobox
        // Hide in edit mode
        if (isEditMode) {
            lbl_group.setVisible(false);
            cbb_group.setVisible(false);
        }
        // Bind managed mode (if the items actually take up space in the layout) to the visibility property
        lbl_group.managedProperty().bindBidirectional(lbl_group.visibleProperty());
        cbb_group.managedProperty().bindBidirectional(cbb_group.visibleProperty());
        cbb_group.setItems(availableGroups);
        cbb_group.getSelectionModel().selectFirst();
        cbb_group.setConverter(new StringConverter<>() {
            @Override
            public String toString(Group object) {
                return object == null ? null : object.getName();
            }

            @Override
            public Group fromString(String string) {
                return cbb_group.getItems().stream().filter(group ->
                    group.getName().equals(string)).findFirst().orElse(null);
            }
        });
        cbb_group.getSelectionModel().selectedItemProperty().addListener((((observable, oldValue, newValue) -> {
            if (newValue == null) {
                return;
            }
            updateTags(newValue.getId());
            if (!newValue.equals(oldValue) && !isEditMode) {
                try {
                    generateEmptyBillShares(newValue.getId(), null);
                    // Update currency
                    billUIModel.setCurrency(Currency.fromId(newValue.getBaseCurrencyID()));
                    // Update base currency labels
                    lbl_base_currency.setText(billUIModel.getGroup() == null ? "Base: - "
                        : "Base: " + Currency.fromId(billUIModel.getGroup().getBaseCurrencyID()).displayFormat());
                    lbl_base_currency_symbol.setText(billUIModel.getGroup() == null ? "-"
                        : Currency.fromId(billUIModel.getGroup().getBaseCurrencyID()).getSymbol());

                    // Special case: Share technique is manual and a new group is selected
                    // Update the display value of the tbl_column_bill_shares_input text to the (new) group base currency
                    if (billUIModel.getShareTechniqueId() != null
                        && billUIModel.getShareTechniqueId().equals(ShareTechniqueId.MANUAL)) {
                        tbl_column_bill_shares_text_property.setValue(
                            Currency.fromId(newValue.getBaseCurrencyID()).displayFormat()
                        );
                    }
                } catch (InvalidArgumentException e) {
                    AlertHelper.invalidArgumentExceptionAlert(e.getMessage());
                } catch (PersistenceException e) {
                    AlertHelper.persistenceExceptionAlert(e.getMessage());
                }
            }
        })));
        // Bind ui model to cbb
        billUIModel.groupProperty().bindBidirectional(cbb_group.valueProperty());

        // TITLE
        txt_title.textProperty().bindBidirectional(billUIModel.titleProperty());


        // AMOUNT
        txt_amount.textProperty().bindBidirectional(billUIModel.amountProperty(), stringMoneyConverter);
        // Select everything when clicking amount field
        txt_amount.focusedProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue != null && newValue) {
                Platform.runLater(() -> {
                    if (txt_amount.isFocused() && !txt_amount.getText().isEmpty()) {
                        txt_amount.selectAll();
                    }
                });
            }
        }));


        // Share Technique
        rb_even.setToggleGroup(shareToggleGroup);
        rb_percent.setToggleGroup(shareToggleGroup);
        rb_absolute.setToggleGroup(shareToggleGroup);
        shareToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue)
            -> {
            if (newValue == null) return;
            billUIModel.setShareTechniqueId(((RadioButton) newValue).getText().equals(rb_even.getText())
                ? ShareTechniqueId.EVEN : (((RadioButton) newValue).getText().equals(rb_percent.getText()) ?
                ShareTechniqueId.PERCENTAGE : ShareTechniqueId.MANUAL));
        });


        // BILL SHARES
        // Set display value for users list view
        tbl_column_bill_shares_select.setCellValueFactory(param -> {
            CheckBox checkBox = new CheckBox();
            checkBox.selectedProperty().bindBidirectional(param.getValue().selectedProperty());
            return new SimpleObjectProperty<>(checkBox);
        });
        tbl_column_bill_shares_input.textProperty().bindBidirectional(tbl_column_bill_shares_text_property);
        billUIModel.shareTechniqueIdProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue == null) return;
            if (newValue.equals(ShareTechniqueId.PERCENTAGE)) {
                tbl_column_bill_shares_text_property.setValue("Share (%)");
            } else if (newValue.equals(ShareTechniqueId.MANUAL)) {
                // Get current currency
                Currency current;
                if (billUIModel.getCurrency() == null)
                    current = Currency.fromId(billUIModel.getGroup().getBaseCurrencyID());
                else
                    current = billUIModel.getCurrency();
                tbl_column_bill_shares_text_property.setValue(current.displayFormat());
            } else {
                // Share technique EVEN -> empty text
            }
        }));
        // Set name column values
        tbl_column_bill_shares_name.setCellValueFactory(cellData
            -> new SimpleStringProperty(cellData.getValue().getUser().getName()));

        // Configure input column
        // Make editable
        tbl_view_bill_shares.setEditable(true);
        tbl_column_bill_shares_input.setEditable(true);

        tbl_column_bill_shares_input
            .setCellValueFactory(cellData
                -> new SimpleStringProperty(stringMoneyConverter.toString(cellData.getValue().amountProperty().get())));
        // Populate the table column with custom text fields
        tbl_column_bill_shares_input.setCellFactory(column -> new EditableTableCell<BillShareUIModel, String>());
        tbl_column_bill_shares_input.setOnEditCommit(event -> {
            // Update bill share ui model
            String amount = event.getNewValue() != null ? event.getNewValue() : event.getOldValue();
            BillShareUIModel share = event.getTableView().getItems().get(event.getTablePosition().getRow());
            share.setAmount(stringMoneyConverter.fromString(amount));
            // Tick checkbox
            share.setSelected(true);
            tbl_view_bill_shares.refresh();
        });
        // Input column is only visible if share technique is not EVEN
        tbl_column_bill_shares_input.visibleProperty().bind(rb_even.selectedProperty().not());
        // Bind the values from the input fields to the ui model
        tbl_view_bill_shares.itemsProperty().bindBidirectional(billShareUIModels);


        // DESCRIPTION
        txt_description.textProperty().bindBidirectional(billUIModel.descriptionProperty());


        // CURRENCY
        cbb_currency.setItems(FXCollections.observableArrayList(Currency.values()));
        // Default select group currency
        billUIModel.setCurrency(Currency.fromId(billUIModel.getGroup().getBaseCurrencyID()));
        txt_exchange_rate.setDisable(true);
        cbb_currency.valueProperty().bindBidirectional(billUIModel.currencyProperty());
        cbb_currency.valueProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue == null || (oldValue != null && oldValue.equals(newValue))) return;
            // Update text field with new exhange rate relative to group base currency
            if(txt_exchange_rate.getText() != null
                && billUIModel.getExchangeRate() != null
                && billUIModel.getExchangeRate().compareTo(stringMoneyConverter.fromString(txt_exchange_rate.getText()))
                != 0) {
                txt_exchange_rate.setText(
                    stringCurrencyConverter
                        .toString(Currency.fromId(billUIModel.getGroup().getBaseCurrencyID()).calculateExchangeRate(newValue))
                );
            }
            // IF a share technique is selected AND the selected share technique is MANUAL update table column text
            if (billUIModel.getShareTechniqueId() != null &&
                billUIModel.getShareTechniqueId().equals(ShareTechniqueId.MANUAL))
                tbl_column_bill_shares_text_property.setValue(
                    newValue.displayFormat());
            // If the currently selected currency is equal to the group's base currency disable editing of exchange
            // rate text field
            if (newValue.equals(Currency.fromId(billUIModel.getGroup().getBaseCurrencyID()))) {
                txt_exchange_rate.setDisable(true);
            } else txt_exchange_rate.setDisable(false);
        }));
        // Custom cbb display value for currency
        cbb_currency.setConverter(new StringConverter<>() {
            @Override
            public String toString(Currency object) {
                return object == null ? null : object.displayFormat();
            }

            @Override
            public Currency fromString(String string) {
                return cbb_currency.getItems().stream().filter(currency ->
                    currency.getName().equals(string)).findFirst().orElse(null);
            }
        });
        // Set exchange rate
        billUIModel.setExchangeRate(BigDecimal.ONE);// Initially always 1
        txt_exchange_rate.textProperty().bindBidirectional(billUIModel.exchangeRateProperty(), stringMoneyConverter);
        // Base currency of group
        lbl_base_currency.setText(billUIModel.getGroup() == null ? "Base: - "
            : "Base: " + Currency.fromId(billUIModel.getGroup().getBaseCurrencyID()).displayFormat());
        lbl_base_currency_symbol.setText(billUIModel.getGroup() == null ? "-"
            : Currency.fromId(billUIModel.getGroup().getBaseCurrencyID()).getSymbol());

        // Tag Combo Box
        setupTags();
    }

    private void setupTags() {
        updateTags();
        cbb_tag.setItems(availableTags);
        cbb_tag.setCellFactory(param -> new ListCell<Tag>() {
            @Override
            protected void updateItem(Tag tag, boolean empty) {
                super.updateItem(tag, empty);

                if (empty || tag == null) {
                    setText("<None>");
                } else {
                    setText(tag.toString());
                }
            }
        });
        billUIModel.tagProperty().bindBidirectional(cbb_tag.valueProperty());
    }

    private void updateTags() {
        updateTags(billUIModel.getGroup().getId());
    }

    private void updateTags(int groupId) {
        try {
            availableTags.clear();
            availableTags.add(null);
            availableTags.addAll(tagService.getTagsForGroup(null, groupId));
        } catch (PersistenceException e) {
            AlertHelper.persistenceExceptionAlert(e.getMessage());
        } catch (InvalidArgumentException e) {
            AlertHelper.invalidArgumentExceptionAlert(e.getMessage());
        }
    }

    @FXML
    private void handleCreate(ActionEvent event) {
        if (!uiValidation(billUIModel)) {
            return;
        }

        try {
            // Sort out unselected users
            List<BillShare> selectedShares = billShareUIModels.get().stream()
                .filter(BillShareUIModel::isSelected)
                .map(BillShareUIModel::toBillShare)
                .collect(Collectors.toList());

            // Check share technique
            ShareTechniqueId technique = billUIModel.getShareTechniqueId();

            // Check if shares are > 0
            if (!technique.equals(ShareTechniqueId.EVEN)) {
                for (BillShare share : selectedShares) {
                    if (share.getAmount() == null || share.getAmount().longValue() <= 0) {
                        AlertHelper.alert(Alert.AlertType.WARNING, "Invalid values specified",
                            "One or more selected shares have invalid values specified. Please specify correct values.");
                        return;
                    }
                }
            }

            if (technique.equals(ShareTechniqueId.EVEN)) {
                for (BillShare selectedShare : selectedShares) {
                    selectedShare.setAmount(BigDecimal.ZERO);
                }
            } else if (technique.equals(ShareTechniqueId.PERCENTAGE)) {
                BigDecimal total = BigDecimal.ZERO; // Must add up to 100
                for (BillShare billShare : selectedShares) {
                    total = total.add(billShare.getAmount());
                }
                if (total.compareTo(BigDecimal.valueOf(100)) != 0) {
                    AlertHelper.alert(Alert.AlertType.WARNING, "Error",
                        "The values for the percentages of you users don't add up to 100%. Please check your values.");
                    return;
                }
            } else if (technique.equals(ShareTechniqueId.MANUAL)) {
                BigDecimal total = BigDecimal.ZERO;
                for (BillShare billShare : selectedShares) {
                    total = total.add(billShare.getAmount());
                }
                if (total.compareTo(billUIModel.getAmount()) != 0) {
                    AlertHelper.alert(Alert.AlertType.WARNING, "Error",
                        "The values for the shares of you users don't add up to "
                            + billUIModel.getAmount() + ". Please check your values.");
                    return;
                }
            }

            Bill bill;
            if (isEditMode) {
                billUIModel.setBillShares(selectedShares);
                billService.editBill(billUIModel);
                isEditMode = false;
                AlertHelper.successAlert("The bill was updated successfully.");
                applicationController.triggerMainStageEnabled();

                stage.close();

            } else {
                // Bill shares are valid -> create bill
                billUIModel.setBillShares(selectedShares);
                bill = billService.createBill(billUIModel, null);

                // Notify user
                AlertHelper.successAlert("The bill was created successfully.");

                // Create event
                eventService.createEvent(new at.ac.tuwien.sepm.fridget.common.entities.Event(
                    EventType.CREATE_BILL,
                    false,
                    LocalDateTime.now(),
                    bill.getUser().getName() + " created bill " + bill.getTitle(),
                    bill.getUser(),
                    null,
                    null,
                    bill,
                    bill.getGroup()
                ));

                // Reset
                resetFields();
            }

        } catch (PersistenceException e) {
            AlertHelper.persistenceExceptionAlert(e.getMessage());
        } catch (InvalidArgumentException e) {
            AlertHelper.invalidArgumentExceptionAlert(e.getMessage());
        }
    }

    /**
     * Basic UI validation of bill
     *
     * @return true if all required fields are set and valid, false otherwise
     */
    private boolean uiValidation(Bill bill) {
        StringBuilder errors = new StringBuilder();
        Alert alert = new Alert(Alert.AlertType.WARNING);

        if (billUIModel.getAmount() == null || billUIModel.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            errors.append("Invalid value for amount.").append(System.lineSeparator());
        }
        if (billUIModel.getTitle() == null) {
            errors.append("Invalid value for title.").append(System.lineSeparator());
        }
        if (billUIModel.getShareTechniqueId() == null) {
            errors.append("Please specify a share technique.").append(System.lineSeparator());
        }
        // Check if at least one user set
        boolean usersValid = false;
        for (BillShareUIModel billShareUIModel : billShareUIModels.get()) {
            if (billShareUIModel.isSelected()) {
                usersValid = true;
                break;
            }
        }
        if (!usersValid) {
            errors.append("Please select at least one user.").append(System.lineSeparator());
        }
        if (billUIModel.getCurrency() == null) {
            errors.append("Please specify a Currency.");
        }
        if (billUIModel.getExchangeRate() == null) {
            errors.append("Please specify a valid exchange rate!");
        } else if (billUIModel.getExchangeRate().compareTo(BigDecimal.ZERO) <= 0) {
            errors.append("Please specify an exchange rate > 0. Negative values for exchange rates are not allowed.");
        }
        if (errors.toString().equals("")) {
            return true;
        } else {
            alert.setTitle("Invalid values");
            alert.setHeaderText("Invalid values");
            alert.setContentText(errors.toString());
            alert.show();
            return false;
        }
    }

    public void setEditMode(boolean editMode) {
        this.isEditMode = editMode;
        if (editMode) {
            // Hide group dropdown
            lbl_group.setVisible(false);
            cbb_group.setVisible(false);
            lbl_title.setText("Edit Bill");
            btn.setText("Edit");
        } else {
            lbl_group.setVisible(true);
            cbb_group.setVisible(true);
            lbl_title.setText("Create Bill");
            btn.setText("Create");
        }
    }

    public void setDialogStage(Stage dialogStage) {
        this.stage = dialogStage;
    }

    public void setBill(BillUIModel billUIModel) {
        this.billUIModel.setDeleted(billUIModel.isDeleted());
        this.billUIModel.setAmount(billUIModel.getAmount());
        this.billUIModel.setTitle(billUIModel.getTitle());
        this.billUIModel.setTag(billUIModel.getTag());
        this.billUIModel.setBillShares(billUIModel.getBillShares());
        this.billUIModel.setExchangeRate(billUIModel.getExchangeRate());
        this.billUIModel.setCurrency(billUIModel.getCurrency());
        this.billUIModel.setUser(billUIModel.getUser());
        this.billUIModel.setDescription(billUIModel.getDescription());
        this.billUIModel.setCreatedAt(billUIModel.getCreatedAt());
        this.billUIModel.setUpdatedAt(billUIModel.getCreatedAt());
        this.billUIModel.setId(billUIModel.getId());
        this.billUIModel.setGroup(billUIModel.getGroup());
        this.billUIModel.setUser(billUIModel.getUser());
        this.billUIModel.setShareTechniqueId(billUIModel.getShareTechniqueId());

        try {
            generateEmptyBillShares(billUIModel.getGroup().getId(), billUIModel.getBillShares());
        } catch (InvalidArgumentException e) {
            AlertHelper.invalidArgumentExceptionAlert(e.getMessage());
        } catch (PersistenceException e) {
            AlertHelper.persistenceExceptionAlert(e.getMessage());
        }

        rb_even.setSelected(billUIModel.getShareTechniqueId() == ShareTechniqueId.EVEN);
        rb_percent.setSelected(billUIModel.getShareTechniqueId() == ShareTechniqueId.PERCENTAGE);
        rb_absolute.setSelected(billUIModel.getShareTechniqueId() == ShareTechniqueId.MANUAL);
    }

    /**
     * Generate empty bill shares to be configured
     *
     * @return
     */
    private void generateEmptyBillShares(int groupId, List<BillShare> billShares) throws InvalidArgumentException, PersistenceException {
        // Reset
        if (billShareUIModels.get() != null) {
            billShareUIModels.get().clear();
        }

        List<User> users = groupService.getUsersForGroup(groupId, -1);
        ObservableList<BillShareUIModel> shareUIModels = FXCollections.observableArrayList();

        List<User> u = new LinkedList<>();
        if (billShares != null) {
            billShares.forEach(billShare -> {
                BillShareUIModel share = modelMapper.map(billShare, BillShareUIModel.class);
                share.setSelected(true);
                u.add(share.getUser());
                shareUIModels.add(share);
            });
        }

        for (User user : users) {
            if (u.contains(user))
                continue;

            BillShareUIModel share = new BillShareUIModel();
            share.setUser(user);
            share.setSelected(false);
            shareUIModels.add(share);
        }

        billShareUIModels.setValue(shareUIModels);
    }

    /**
     * Reset all user input fields to their default values
     */
    private void resetFields() {
        billUIModel = new BillUIModel();
        billShareUIModels.setValue(FXCollections.observableArrayList());
        cbb_group.getSelectionModel().clearSelection();
        txt_title.clear();
        txt_amount.clear();
        shareToggleGroup.getSelectedToggle().setSelected(false);
        tbl_view_bill_shares.refresh();
        txt_description.clear();
        this.initialize(null, null);
    }

    public void closeStage() {
        stage.close();
    }

    public void handleUploadBill(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select an Image with your Bill");
        fileChooser.getExtensionFilters().setAll(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"),
            new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        File file = fileChooser.showOpenDialog(uiService.getPrimaryStage());
        if (file != null) {
            try {
                BigDecimal sum = ocrService.extractSum(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
                if (sum == null) {
                    AlertHelper.alert(Alert.AlertType.ERROR, "OCR Error", "Could not extract the sum from the image");
                } else {
                    billUIModel.setAmount(sum);
                    AlertHelper.successAlert("Bill amount successfully uploaded!");
                }
            } catch (IOException e) {
                AlertHelper.ioExceptionAlert(e.getMessage());
            }
        }
    }

    /**
     * Custom editable table column for bill shares
     */
    private static class EditableTableCell<S, T> extends TextFieldTableCell<S, T> {

        private final TextField editableTextField;

        EditableTableCell() {
            super();
            editableTextField = new TextField();
            editableTextField.setOnKeyPressed(new EventHandler<KeyEvent>() {
                @Override
                public void handle(KeyEvent event) {

                    if (event.getCode().equals(KeyCode.ENTER))
                        commitEdit((T) editableTextField.getText());
                }
            });
            editableTextField.focusedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {

                    if (!newValue)
                        commitEdit((T) editableTextField.getText());
                }
            });
            editableTextField.textProperty().bindBidirectional(textProperty());
        }

        @Override
        public void startEdit() {
            super.startEdit();
            setGraphic(editableTextField);
        }

        @Override
        public void cancelEdit() {
            super.cancelEdit();
            setGraphic(null);
        }

        @Override
        public void updateItem(final T item, final boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setText(null);
                setGraphic(null);
            } else {
                if (item == null) {
                    setGraphic(null);
                } else {
                    if (isEditing()) {
                        setGraphic(editableTextField);
                        setText((String) getItem());
                    } else {
                        setGraphic(null);
                        setText((String) getItem());
                    }
                }
            }
        }

        @Override
        public void commitEdit(T item) {
            if (!isEditing() && !item.equals(getItem())) {
                TableView<S> table = getTableView();
                if (table != null) {
                    TableColumn<S, T> column = getTableColumn();
                    TableColumn.CellEditEvent<S, T> event = new TableColumn.CellEditEvent<>(
                        table, new TablePosition<S, T>(table, getIndex(), column),
                        TableColumn.editCommitEvent(), item
                    );
                    Event.fireEvent(column, event);
                    updateItem(item, false);
                }
            }
            super.commitEdit(item);
        }

    }
}
