package at.ac.tuwien.sepm.fridget.client.controllers;

import at.ac.tuwien.sepm.fridget.client.services.ClientBillService;
import at.ac.tuwien.sepm.fridget.client.uimodels.GroupBalanceUiModel;
import at.ac.tuwien.sepm.fridget.client.util.AlertHelper;
import at.ac.tuwien.sepm.fridget.common.entities.Bill;
import at.ac.tuwien.sepm.fridget.common.entities.BillShare;
import at.ac.tuwien.sepm.fridget.common.entities.Group;
import at.ac.tuwien.sepm.fridget.common.exception.InvalidArgumentException;
import at.ac.tuwien.sepm.fridget.common.exception.PersistenceException;
import at.ac.tuwien.sepm.fridget.common.util.Currency;
import at.ac.tuwien.sepm.fridget.common.util.ShareTechniqueId;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

@Component
public class DialogController implements Initializable {

    @FXML
    public Label label_textForSettlingUp;
    @FXML
    public Label label_memberName;
    @FXML
    public Label label_AmountMember;
    @FXML
    public TextField txt_amount;
    @FXML
    public TextField txt_paymentMethod;
    @Autowired
    private ClientBillService billService;
    private Stage stage;

    private GroupBalanceUiModel memberToSettle;

    private Group group;
    private BigDecimal amount;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    public void setFields(Group group) {
        this.group = group;
        if (isNegative(memberToSettle.getBalance())) {
            label_AmountMember.setText(memberToSettle.getBalance().negate().toString());
        } else {
            label_AmountMember.setText(memberToSettle.getBalance().toString());
        }
        label_memberName.setText(memberToSettle.getUser().getName());
        if (isNegative(memberToSettle.getBalance())) {
            label_textForSettlingUp.setText("You owe them: ");
        } else {
            label_textForSettlingUp.setText("They owe you: ");
        }

    }

    private boolean isNegative(BigDecimal value) {
        return value.signum() == -1;
    }

    public void setDialogStage(Stage dialogStage) {
        this.stage = dialogStage;
    }

    public void setMemberToSettle(GroupBalanceUiModel user) {
        this.memberToSettle = user;
    }

    @FXML
    public void handleSettleUpButton()  {
        if (txt_amount.getText().isEmpty()) {
            AlertHelper.invalidArgumentExceptionAlert("Amount can't be empty");
        }

        DecimalFormat df = new DecimalFormat("#,##0.00");
        df.setParseBigDecimal(true);
        try {
            amount = (BigDecimal)df.parse(txt_amount.getText());
        } catch (ParseException e) {
            AlertHelper.invalidArgumentExceptionAlert("Invalid number format!");
            return;
        }

        if (txt_paymentMethod.getText().isEmpty()) {
            AlertHelper.invalidArgumentExceptionAlert("The Payment Method can't be empty");
        } else {
            if (isNegative(memberToSettle.getBalance())) {
                if (memberToSettle.getBalance().compareTo(amount.negate()) <= 0) {
                    createBill();
                } else {
                    AlertHelper.invalidArgumentExceptionAlert("You can't give them more money then you owe them");
                }
            } else {
                if (memberToSettle.getBalance().compareTo(amount) >= 0) {
                    createBill();
                } else {
                    AlertHelper.invalidArgumentExceptionAlert("You can't give them more money then they owe you");
                }
            }
        }
    }

    private void createBill() {
        try {
            Bill bill = new Bill();
            bill.setAmount(amount);
            bill.setShareTechniqueId(ShareTechniqueId.EVEN);
            bill.setTitle("Settle Up");
            bill.setDescription(txt_paymentMethod.getText());
            bill.setCreatedAt(Timestamp.from(Instant.now()).toLocalDateTime());
            bill.setDeleted(false);
            bill.setCurrency(Currency.fromId(group.getBaseCurrencyID()));
            bill.setExchangeRate(new BigDecimal(1));
            bill.setGroup(group);

            // bill share
            BillShare billShare = new BillShare();
            billShare.setUser(memberToSettle.getUser());
            bill.setBillShares(Arrays.asList(billShare));
            billService.createBill(bill, null);
            AlertHelper.successAlert("Settled up!");
            stage.close();
        } catch (PersistenceException e) {
            AlertHelper.persistenceExceptionAlert(e.getMessage());
        } catch (InvalidArgumentException e) {
            AlertHelper.invalidArgumentExceptionAlert(e.getMessage());
        }
    }


    @FXML
    public void handleCancelButton() {
        stage.close();
    }
}
