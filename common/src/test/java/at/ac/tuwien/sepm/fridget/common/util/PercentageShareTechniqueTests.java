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

public class PercentageShareTechniqueTests {

    private static ShareTechnique shareTechnique = new PercentageShareTechnique();

    @Test
    public void percentageShareTechnique_returnsValidAmounts() {
        Bill bill = new Bill();
        bill.setAmount(new BigDecimal(10));
        bill.setCurrency(Currency.EUR);
        bill.setExchangeRate(BigDecimal.ONE);
        bill.setUser(new User(1));
        bill.setBillShares(Arrays.asList(
            new BillShare(-1, new User(1), new BigDecimal(20)),
            new BillShare(-1, new User(2), new BigDecimal(20)),
            new BillShare(-1, new User(3), new BigDecimal(20)),
            new BillShare(-1, new User(4), new BigDecimal(20)),
            new BillShare(-1, new User(5), new BigDecimal(20))
        ));

        List<Transaction> transactions = shareTechnique.computeTransactions(bill);

        assertThat(transactions.size()).isEqualTo(5);
        assertThat(transactions.get(0).getAmount()).isEqualTo(new BigDecimal(2));
        assertThat(transactions.get(0).getUserPaidId()).isEqualTo(1);
        assertThat(transactions.get(0).getUserReceivedId()).isEqualTo(1);
        assertThat(transactions.get(1).getAmount()).isEqualTo(new BigDecimal(2));
        assertThat(transactions.get(1).getUserPaidId()).isEqualTo(1);
        assertThat(transactions.get(1).getUserReceivedId()).isEqualTo(2);
        assertThat(transactions.get(2).getAmount()).isEqualTo(new BigDecimal(2));
        assertThat(transactions.get(2).getUserPaidId()).isEqualTo(1);
        assertThat(transactions.get(2).getUserReceivedId()).isEqualTo(3);
        assertThat(transactions.get(3).getAmount()).isEqualTo(new BigDecimal(2));
        assertThat(transactions.get(3).getUserPaidId()).isEqualTo(1);
        assertThat(transactions.get(3).getUserReceivedId()).isEqualTo(4);
        assertThat(transactions.get(4).getAmount()).isEqualTo(new BigDecimal(2));
        assertThat(transactions.get(4).getUserPaidId()).isEqualTo(1);
        assertThat(transactions.get(4).getUserReceivedId()).isEqualTo(5);
    }

    @Test
    public void percentageShareTechnique_splitsExactAmount() {
        Bill bill = new Bill();
        bill.setAmount(new BigDecimal("10.01"));
        bill.setCurrency(Currency.EUR);
        bill.setExchangeRate(BigDecimal.ONE);
        bill.setUser(new User(1));
        bill.setBillShares(Arrays.asList(
            new BillShare(-1, new User(1), new BigDecimal(50)),
            new BillShare(-1, new User(2), new BigDecimal(50))
        ));

        List<Transaction> transactions = shareTechnique.computeTransactions(bill);

        assertThat(transactions.size()).isEqualTo(2);
        assertThat(transactions.get(0).getAmount()).isEqualTo(new BigDecimal("5"));
        assertThat(transactions.get(0).getUserPaidId()).isEqualTo(1);
        assertThat(transactions.get(0).getUserReceivedId()).isEqualTo(1);
        assertThat(transactions.get(1).getAmount()).isEqualTo(new BigDecimal("5.01"));
        assertThat(transactions.get(1).getUserPaidId()).isEqualTo(1);
        assertThat(transactions.get(1).getUserReceivedId()).isEqualTo(2);
    }

}
