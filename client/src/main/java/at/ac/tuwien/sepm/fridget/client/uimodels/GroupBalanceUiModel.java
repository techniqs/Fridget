package at.ac.tuwien.sepm.fridget.client.uimodels;

import at.ac.tuwien.sepm.fridget.common.entities.User;

import java.math.BigDecimal;

public class GroupBalanceUiModel {

    private User user;
    private BigDecimal balance;


    public GroupBalanceUiModel(User user, BigDecimal balance) {
        this.user = user;
        this.balance = balance;
    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

}
