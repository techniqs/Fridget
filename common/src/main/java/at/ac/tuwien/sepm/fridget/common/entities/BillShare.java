package at.ac.tuwien.sepm.fridget.common.entities;

import java.math.BigDecimal;

public class BillShare {

    private int id;
    private User user;
    private BigDecimal amount;
    private Bill bill;


    public BillShare() {
        this(-1, null, BigDecimal.ZERO);
    }

    public BillShare(int id, User user, BigDecimal amount) {
        this.id = id;
        this.user = user;
        this.amount = amount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Bill getBill() {
        return bill;
    }

    public void setBill(Bill bill) {
        this.bill = bill;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof BillShare) {
            BillShare share = (BillShare) obj;
            return share.getId() == this.id;
        }
        return false;
    }
}
