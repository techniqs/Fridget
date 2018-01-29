package at.ac.tuwien.sepm.fridget.common.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaction {

    private int id = -1;
    private LocalDateTime createdAt;
    private BigDecimal amount;
    private int billId = -1;
    private int userPaidId = -1;
    private int userReceivedId = -1;

    public Transaction() {
    }

    public Transaction(int billId, BigDecimal amount, int userPaidId, int userReceivedId) {
        this.billId = billId;
        this.amount = amount;
        this.userPaidId = userPaidId;
        this.userReceivedId = userReceivedId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public int getBillId() {
        return billId;
    }

    public void setBillId(int billId) {
        this.billId = billId;
    }

    public int getUserPaidId() {
        return userPaidId;
    }

    public void setUserPaidId(int userPaidId) {
        this.userPaidId = userPaidId;
    }

    public int getUserReceivedId() {
        return userReceivedId;
    }

    public void setUserReceivedId(int userReceivedId) {
        this.userReceivedId = userReceivedId;
    }

    @Override
    public String toString() {
        return "Transaction{" +
            "id=" + id +
            ", createdAt=" + createdAt +
            ", amount=" + amount +
            ", billId=" + billId +
            ", userPaidId=" + userPaidId +
            ", userReceivedId=" + userReceivedId +
            '}';
    }
}
