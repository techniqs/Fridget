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

public class ManualShareTechniqueTests {

    private static ShareTechnique shareTechnique = new ManualShareTechnique();

    @Test
    public void manualShareTechnique_returnsValidAmounts() {
        Bill bill = new Bill();
        bill.setAmount(new BigDecimal(150));
        bill.setCurrency(Currency.EUR);
        bill.setExchangeRate(BigDecimal.ONE);
        bill.setUser(new User(1));
        bill.setBillShares(Arrays.asList(
            new BillShare(-1, new User(1), new BigDecimal(10)),
            new BillShare(-1, new User(2), new BigDecimal(20)),
            new BillShare(-1, new User(3), new BigDecimal(30)),
            new BillShare(-1, new User(4), new BigDecimal(40)),
            new BillShare(-1, new User(5), new BigDecimal(50))
        ));

        List<Transaction> transactions = shareTechnique.computeTransactions(bill);

        assertThat(transactions.size()).isEqualTo(5);
        assertThat(transactions.get(0).getAmount()).isEqualByComparingTo(new BigDecimal(10));
        assertThat(transactions.get(0).getUserPaidId()).isEqualTo(1);
        assertThat(transactions.get(0).getUserReceivedId()).isEqualTo(1);
        assertThat(transactions.get(1).getAmount()).isEqualTo(new BigDecimal(20));
        assertThat(transactions.get(1).getUserPaidId()).isEqualTo(1);
        assertThat(transactions.get(1).getUserReceivedId()).isEqualTo(2);
        assertThat(transactions.get(2).getAmount()).isEqualTo(new BigDecimal(30));
        assertThat(transactions.get(2).getUserPaidId()).isEqualTo(1);
        assertThat(transactions.get(2).getUserReceivedId()).isEqualTo(3);
        assertThat(transactions.get(3).getAmount()).isEqualTo(new BigDecimal(40));
        assertThat(transactions.get(3).getUserPaidId()).isEqualTo(1);
        assertThat(transactions.get(3).getUserReceivedId()).isEqualTo(4);
        assertThat(transactions.get(4).getAmount()).isEqualTo(new BigDecimal(50));
        assertThat(transactions.get(4).getUserPaidId()).isEqualTo(1);
        assertThat(transactions.get(4).getUserReceivedId()).isEqualTo(5);
    }

}
