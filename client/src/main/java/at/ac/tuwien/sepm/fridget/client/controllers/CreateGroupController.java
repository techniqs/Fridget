package at.ac.tuwien.sepm.fridget.client.controllers;

import at.ac.tuwien.sepm.fridget.client.services.ClientGroupService;
import at.ac.tuwien.sepm.fridget.client.uimodels.GroupUIModel;
import at.ac.tuwien.sepm.fridget.client.util.AlertHelper;
import at.ac.tuwien.sepm.fridget.client.util.GroupUiValidationUtil;
import at.ac.tuwien.sepm.fridget.common.exception.InvalidArgumentException;
import at.ac.tuwien.sepm.fridget.common.exception.PersistenceException;
import at.ac.tuwien.sepm.fridget.common.util.Currency;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
public class CreateGroupController implements Initializable {

    @Autowired
    private ClientGroupService groupService;

    // UI element for binding UI data
    private GroupUIModel groupUIModel = new GroupUIModel();

    @FXML
    public TextField txt_groupName;

    @FXML
    private ComboBox<Currency> cbb_base_currency;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        txt_groupName.textProperty().bindBidirectional(groupUIModel.nameProperty());

        cbb_base_currency.setItems(FXCollections.observableArrayList(Currency.values()));
        cbb_base_currency.setConverter(new StringConverter<>() {
            @Override
            public String toString(Currency object) {
                return object == null ? null : object.displayFormat();
            }

            @Override
            public Currency fromString(String string) {
                return cbb_base_currency.getItems().stream().filter(currency ->
                    currency.getName().equals(string)).findFirst().orElse(null);
            }
        });
        cbb_base_currency.valueProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue == null) return;
            groupUIModel.setBaseCurrencyID(newValue.getId());
        }));

    }

    @FXML
    public void handleCreate() {
        if (!GroupUiValidationUtil.uiValidation(groupUIModel)) {
            return;
        }
        if (cbb_base_currency.getValue() == null) {
            AlertHelper.alert(Alert.AlertType.WARNING, "Select base currency", "A Base currency is " +
                "mandatory for the creation of a group. Please select a base currency.");
            return;
        }
        try {
            groupService.createGroup(groupUIModel, -1);
            AlertHelper.alert(Alert.AlertType.INFORMATION, "Success!", "Group successfully created");
        } catch (PersistenceException e) {
            AlertHelper.persistenceExceptionAlert(e.getMessage());
        } catch (InvalidArgumentException e) {
            AlertHelper.invalidArgumentExceptionAlert(e.getMessage());
        }
    }
}
