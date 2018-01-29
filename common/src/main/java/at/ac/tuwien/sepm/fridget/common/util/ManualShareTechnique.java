package at.ac.tuwien.sepm.fridget.common.util;

import at.ac.tuwien.sepm.fridget.common.entities.Bill;
import at.ac.tuwien.sepm.fridget.common.entities.Transaction;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

public class ManualShareTechnique implements ShareTechnique {

    @Override
    public List<Transaction> computeTransactions(Bill bill) {
        return bill.getBillShares().stream().map(billShare -> new Transaction(
            bill.getId(),
            new BigDecimal(billShare.getAmount().multiply(bill.getExchangeRate())
                .setScale(2, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString()),
            bill.getUser().getId(),
            billShare.getUser().getId()
        )).collect(Collectors.toList());
    }
}
