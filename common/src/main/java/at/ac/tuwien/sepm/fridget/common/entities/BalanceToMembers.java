package at.ac.tuwien.sepm.fridget.common.entities;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class BalanceToMembers {

    private Map<Integer, BigDecimal> balances;


    public BalanceToMembers() {
        this(new HashMap<>());
    }

    public BalanceToMembers(Map<Integer, BigDecimal> balances) {
        this.balances = balances;
    }


    public Map<Integer, BigDecimal> getBalances() {
        return balances;
    }

    public void setBalances(Map<Integer, BigDecimal> balances) {
        this.balances = balances;
    }

}
