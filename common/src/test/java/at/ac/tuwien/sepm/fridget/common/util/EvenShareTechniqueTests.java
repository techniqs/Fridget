package at.ac.tuwien.sepm.fridget.common.util;

import at.ac.tuwien.sepm.fridget.common.entities.Bill;
import at.ac.tuwien.sepm.fridget.common.entities.BillShare;
import at.ac.tuwien.sepm.fridget.common.entities.Transaction;
import at.ac.tuwien.sepm.fridget.common.entities.User;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class EvenShareTechniqueTests {

    private static ShareTechnique shareTechnique = new EvenShareTechnique();

    @Test
    public void evenShareTechnique_splitsExactAmount() {
        Bill bill = new Bill();
        bill.setAmount(new BigDecimal(10));
        bill.setCurrency(Currency.EUR);
        bill.setExchangeRate(BigDecimal.ONE);
        bill.setUser(new User(1));
        bill.setBillShares(Arrays.asList(
            new BillShare(-1, new User(1), null),
            new BillShare(-1, new User(2), null),
            new BillShare(-1, new User(3), null)
        ));

        List<Transaction> transactions = shareTechnique.computeTransactions(bill);

        assertThat(transactions.size()).isEqualTo(3);
        assertThat(transactions.get(0).getAmount()).isEqualTo(new BigDecimal("3.34"));
        assertThat(transactions.get(0).getUserPaidId()).isEqualTo(1);
        assertThat(transactions.get(0).getUserReceivedId()).isEqualTo(1);
        assertThat(transactions.get(1).getAmount()).isEqualTo(new BigDecimal("3.33"));
        assertThat(transactions.get(1).getUserPaidId()).isEqualTo(1);
        assertThat(transactions.get(1).getUserReceivedId()).isEqualTo(2);
        assertThat(transactions.get(2).getAmount()).isEqualTo(new BigDecimal("3.33"));
        assertThat(transactions.get(2).getUserPaidId()).isEqualTo(1);
        assertThat(transactions.get(2).getUserReceivedId()).isEqualTo(3);
    }

    @Test
    public void evenShareTechnique_splitsRestAcrossManyUsers() {
        Bill bill = new Bill();
        bill.setAmount(new BigDecimal("10.01"));
        bill.setCurrency(Currency.EUR);
        bill.setExchangeRate(BigDecimal.ONE);
        bill.setUser(new User(1));
        bill.setBillShares(Arrays.asList(
            new BillShare(-1, new User(1), null),
            new BillShare(-1, new User(2), null),
            new BillShare(-1, new User(3), null)
        ));

        List<Transaction> transactions = shareTechnique.computeTransactions(bill);

        assertThat(transactions.size()).isEqualTo(3);
        assertThat(transactions.get(0).getAmount()).isEqualTo(new BigDecimal("3.34"));
        assertThat(transactions.get(0).getUserPaidId()).isEqualTo(1);
        assertThat(transactions.get(0).getUserReceivedId()).isEqualTo(1);
        assertThat(transactions.get(1).getAmount()).isEqualTo(new BigDecimal("3.34"));
        assertThat(transactions.get(1).getUserPaidId()).isEqualTo(1);
        assertThat(transactions.get(1).getUserReceivedId()).isEqualTo(2);
        assertThat(transactions.get(2).getAmount()).isEqualTo(new BigDecimal("3.33"));
        assertThat(transactions.get(2).getUserPaidId()).isEqualTo(1);
        assertThat(transactions.get(2).getUserReceivedId()).isEqualTo(3);
    }
}
