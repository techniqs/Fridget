package at.ac.tuwien.sepm.fridget.common.util;

import at.ac.tuwien.sepm.fridget.common.entities.Bill;
import at.ac.tuwien.sepm.fridget.common.entities.BillShare;
import at.ac.tuwien.sepm.fridget.common.entities.Transaction;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class PercentageShareTechnique implements ShareTechnique {

    @Override
    public List<Transaction> computeTransactions(Bill bill) {
        BigDecimal totalAmount = bill.getAmount().multiply((bill.getExchangeRate()))
            .setScale(2, RoundingMode.HALF_UP).stripTrailingZeros();
        BigDecimal currentSum = BigDecimal.ZERO;
        int billShareCount = bill.getBillShares().size();
        List<Transaction> transactions = new ArrayList<>(billShareCount);
        for (int i = 0; i < billShareCount; i++) {
            BillShare billShare = bill.getBillShares().get(i);
            if (i < billShareCount - 1) {
                BigDecimal fraction = totalAmount
                    .multiply(billShare.getAmount().multiply(new BigDecimal("0.01")))
                    .setScale(2, RoundingMode.FLOOR).stripTrailingZeros();
                currentSum = currentSum.add(fraction);
                transactions.add(new Transaction(
                    bill.getId(),
                    fraction.stripTrailingZeros(),
                    bill.getUser().getId(),
                    billShare.getUser().getId()
                ));
            } else {
                // Last person gets everything left
                transactions.add(new Transaction(
                    bill.getId(),
                    totalAmount.subtract(currentSum).stripTrailingZeros(),
                    bill.getUser().getId(),
                    billShare.getUser().getId()
                ));
            }
        }
        return transactions;
    }
}
