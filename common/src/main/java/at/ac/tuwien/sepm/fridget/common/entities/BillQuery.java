package at.ac.tuwien.sepm.fridget.common.entities;

import at.ac.tuwien.sepm.fridget.common.util.Currency;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BillQuery {

    private String text;
    private User payer;
    private Group group;
    private LocalDateTime minDate;
    private LocalDateTime maxDate;
    private BigDecimal minAmount;
    private BigDecimal maxAmount;
    private Currency currency;
    private Tag tag;
    private boolean deleted;
    private boolean valid;


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public User getPayer() {
        return payer;
    }

    public void setPayer(User payer) {
        this.payer = payer;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public LocalDateTime getMinDate() {
        return minDate;
    }

    public void setMinDate(LocalDateTime minDate) {
        this.minDate = minDate;
    }

    public LocalDateTime getMaxDate() {
        return maxDate;
    }

    public void setMaxDate(LocalDateTime maxDate) {
        this.maxDate = maxDate;
    }

    public BigDecimal getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(BigDecimal minAmount) {
        this.minAmount = minAmount;
    }

    public BigDecimal getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(BigDecimal maxAmount) {
        this.maxAmount = maxAmount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }
}
