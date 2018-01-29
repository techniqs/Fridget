package at.ac.tuwien.sepm.fridget.common.util;

import at.ac.tuwien.sepm.fridget.common.entities.Bill;
import at.ac.tuwien.sepm.fridget.common.entities.BillShare;
import at.ac.tuwien.sepm.fridget.common.entities.Transaction;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class EvenShareTechnique implements ShareTechnique {

    @Override
    public List<Transaction> computeTransactions(Bill bill) {
        BigDecimal totalAmount = bill.getAmount().multiply(bill.getExchangeRate())
            .setScale(2, RoundingMode.HALF_UP).stripTrailingZeros();
        int billShareCount = bill.getBillShares().size();

        // Determine the default share per user by dividing and rounding down:
        BigDecimal defaultShare = BigDecimalHelpers.divideMoney(totalAmount, billShareCount).stripTrailingZeros();

        // restUsers determines how many users get an additional cent to pay:
        int restUsers = totalAmount.subtract(defaultShare.multiply(new BigDecimal(billShareCount)))
            .multiply(new BigDecimal(100)).intValue();

        List<Transaction> transactions = new ArrayList<>(billShareCount);
        for (int i = 0; i < billShareCount; i++) {
            BillShare billShare = bill.getBillShares().get(i);
            transactions.add(new Transaction(
                bill.getId(),
                i < restUsers
                    ? defaultShare.add(new BigDecimal("0.01")).stripTrailingZeros()
                    : defaultShare,
                bill.getUser().getId(),
                billShare.getUser().getId()
            ));
        }
        return transactions;
    }

}
