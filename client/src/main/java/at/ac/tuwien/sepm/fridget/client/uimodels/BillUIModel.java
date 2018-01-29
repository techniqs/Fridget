package at.ac.tuwien.sepm.fridget.client.uimodels;

import at.ac.tuwien.sepm.fridget.common.entities.*;
import at.ac.tuwien.sepm.fridget.common.util.Currency;
import at.ac.tuwien.sepm.fridget.common.util.ShareTechniqueId;
import javafx.beans.property.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class BillUIModel extends Bill {
    private IntegerProperty id;
    private ObjectProperty<User> payer;
    private ObjectProperty<BigDecimal> amount;
    private ObjectProperty<ShareTechniqueId> shareTechniqueId;
    private StringProperty title;
    private StringProperty description;
    private ObjectProperty<LocalDateTime> createdAt;
    private ObjectProperty<LocalDateTime> updatedAt;
    private BooleanProperty deleted;
    private ObjectProperty<Currency> currency;
    private ObjectProperty<BigDecimal> exchangeRate;
    private ObjectProperty<List<BillShare>> billShares;
    private ObjectProperty<Tag> tag;
    private ObjectProperty<Group> group;

    public BillUIModel() {
        this.id = new SimpleIntegerProperty();
        this.payer = new SimpleObjectProperty<>();
        this.amount = new SimpleObjectProperty<>();
        this.shareTechniqueId = new SimpleObjectProperty<>();
        this.title = new SimpleStringProperty();
        this.description = new SimpleStringProperty();
        this.createdAt = new SimpleObjectProperty<>();
        this.updatedAt = new SimpleObjectProperty<>();
        this.deleted = new SimpleBooleanProperty();
        this.currency = new SimpleObjectProperty<>();
        this.exchangeRate = new SimpleObjectProperty<>();
        this.billShares = new SimpleObjectProperty<>();
        this.tag = new SimpleObjectProperty<>();
        this.group = new SimpleObjectProperty<>();
    }

    public int getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public User getUser() {
        return payer.get();
    }

    public ObjectProperty<User> payerProperty() {
        return payer;
    }

    public void setUser(User user) {
        this.payer.set(user);
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

    public ShareTechniqueId getShareTechniqueId() {
        return shareTechniqueId.get();
    }

    public ObjectProperty<ShareTechniqueId> shareTechniqueIdProperty() {
        return shareTechniqueId;
    }

    public void setShareTechniqueId(ShareTechniqueId shareTechniqueId) {
        this.shareTechniqueId.set(shareTechniqueId);
    }

    public String getTitle() {
        return title.get();
    }

    public StringProperty titleProperty() {
        return title;
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public String getDescription() {
        return description.get();
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public LocalDateTime getCreatedAt() {
        return createdAt.get();
    }

    public ObjectProperty<LocalDateTime> createdAtProperty() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt.set(createdAt);
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt.get();
    }

    public ObjectProperty<LocalDateTime> updatedAtProperty() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt.set(updatedAt);
    }

    public boolean isDeleted() {
        return deleted.get();
    }

    public BooleanProperty deletedProperty() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted.set(deleted);
    }

    public Currency getCurrency() {
        return currency.get();
    }

    public ObjectProperty<Currency> currencyProperty() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency.set(currency);
    }

    public BigDecimal getExchangeRate() {
        return exchangeRate.get();
    }

    public ObjectProperty<BigDecimal> exchangeRateProperty() {
        return exchangeRate;
    }

    public void setExchangeRate(BigDecimal exchangeRate) {
        this.exchangeRate.set(exchangeRate);
    }

    public List<BillShare> getBillShares() {
        return billShares.get();
    }

    public ObjectProperty<List<BillShare>> billSharesProperty() {
        return billShares;
    }

    public void setBillShares(List<BillShare> billShares) {
        this.billShares.set(billShares);
    }

    public Tag getTag() {
        return tag.get();
    }

    public ObjectProperty<Tag> tagProperty() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag.set(tag);
    }

    @Override
    public Group getGroup() {
        return group.get();
    }

    public ObjectProperty<Group> groupProperty() {
        return group;
    }

    public void setGroup(Group group) {
        this.group.set(group);
    }
}
