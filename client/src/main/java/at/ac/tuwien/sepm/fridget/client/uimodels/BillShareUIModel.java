package at.ac.tuwien.sepm.fridget.client.uimodels;

import at.ac.tuwien.sepm.fridget.common.entities.Bill;
import at.ac.tuwien.sepm.fridget.common.entities.BillShare;
import at.ac.tuwien.sepm.fridget.common.entities.User;
import javafx.beans.property.*;

import java.math.BigDecimal;

public class BillShareUIModel extends BillShare {
    private IntegerProperty id;
    private ObjectProperty<User> user;
    private ObjectProperty<BigDecimal> amount;
    private ObjectProperty<Bill> bill;

    // For selection marking in UI
    private BooleanProperty selected;

    public BillShareUIModel() {
        this.id = new SimpleIntegerProperty();
        this.user = new SimpleObjectProperty<>();
        this.amount = new SimpleObjectProperty<>();
        this.bill = new SimpleObjectProperty<>();
        this.selected = new SimpleBooleanProperty();
    }

    public BillShare toBillShare() {
        BillShare result = new BillShare();
        result.setId(this.getId());
        result.setUser(this.getUser());
        result.setAmount(this.getAmount());
        result.setBill(this.getBill());
        return result;
    }

    public int getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public void setId(int id) {
        super.setId(id);
        this.id.set(id);
    }

    public User getUser() {
        return user.get();
    }

    public ObjectProperty<User> userProperty() {
        return user;
    }

    public void setUser(User user) {
        this.user.set(user);
    }

    public BigDecimal getAmount() {
        return amount.get();
    }

    public ObjectProperty<BigDecimal> amountProperty() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount.set(amount);
    }

    public Bill getBill() {
        return bill.get();
    }

    public ObjectProperty<Bill> billProperty() {
        return bill;
    }

    public void setBill(Bill bill) {
        this.bill.set(bill);
    }

    public boolean isSelected() {
        return selected.get();
    }

    public BooleanProperty selectedProperty() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected.set(selected);
    }
}
