package at.ac.tuwien.sepm.fridget.server.services;

import at.ac.tuwien.sepm.fridget.common.entities.Bill;
import at.ac.tuwien.sepm.fridget.common.entities.BillQuery;
import at.ac.tuwien.sepm.fridget.common.entities.BillShare;
import at.ac.tuwien.sepm.fridget.common.entities.User;
import at.ac.tuwien.sepm.fridget.common.exception.InvalidArgumentException;
import at.ac.tuwien.sepm.fridget.common.exception.PersistenceException;
import at.ac.tuwien.sepm.fridget.common.services.BillService;
import at.ac.tuwien.sepm.fridget.common.services.BillShareService;
import at.ac.tuwien.sepm.fridget.common.services.GroupService;
import at.ac.tuwien.sepm.fridget.server.persistence.BillDAO;
import at.ac.tuwien.sepm.fridget.server.persistence.BillShareDAO;
import at.ac.tuwien.sepm.fridget.server.persistence.TransactionDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service("billService")
public class ServerBillService implements BillService {

    @Autowired
    BillDAO billDAO;

    @Autowired
    TransactionDAO transactionDAO;

    @Autowired
    BillShareDAO billShareDAO;

    @Autowired
    GroupService groupService;

    @Autowired
    BillShareService billShareService;

    @Override
    public Bill createBill(Bill bill, User user) throws PersistenceException, InvalidArgumentException {
        if (!validateBill(bill)) {
            throw new InvalidArgumentException("Bill validation failed.");
        }
        if (!groupService.hasMember(bill.getGroup().getId(), user.getId())) {
            throw new InvalidArgumentException("Bill creator not in selected group.");
        }
        bill.setUser(user);
        bill = billDAO.createBill(bill);
        for (BillShare billShare : bill.getBillShares()) {
            billShare.setBill(bill);
            billShareService.createBillShare(billShare, bill.getId());
        }
        transactionDAO.createTransactions(bill.getShareTechniqueId().getShareTechnique().computeTransactions(bill));
        bill.setBillShares(null);
        return bill;
    }

    @Override
    public Bill editBill(Bill bill) throws PersistenceException, InvalidArgumentException {
        if (validateBill(bill)) {
            transactionDAO.deleteTransactionsForBillId(bill.getId());
            billShareDAO.deleteBillSharesForBillId(bill.getId());
            bill = billDAO.editBill(bill);
            transactionDAO.createTransactions(bill.getShareTechniqueId().getShareTechnique().computeTransactions(bill));
            List<BillShare> updatedShares = new ArrayList<>();
            for (BillShare share : bill.getBillShares()) {
                updatedShares.add(billShareDAO.createBillShare(share, bill.getId()));
            }
            bill.setBillShares(updatedShares);
            //bill.setBillShares(null);
            return bill;
        }
        return null;
    }

    @Override
    public boolean deleteBill(int billID) throws InvalidArgumentException, PersistenceException {
        if (billID < 0)
            throw new InvalidArgumentException("Bill id must not be negativ!");
        transactionDAO.deleteTransactionsForBillId(billID);

        return billDAO.deleteBill(billID);
    }

    @Override
    public boolean undoDeleteBill(int billID) throws InvalidArgumentException, PersistenceException {
        if (billID < 0)
            throw new InvalidArgumentException("Bill id must not be negativ!");
        boolean successful = billDAO.undoDeleteBill(billID);
        Bill bill = billDAO.findBillById(billID);
        bill.setBillShares(billShareService.getAllBillSharesForBill(bill));
        transactionDAO.createTransactions(bill.getShareTechniqueId().getShareTechnique().computeTransactions(bill));
        return successful;
    }

    @Override
    public List<Bill> listAllBills(BillQuery billQuery) throws PersistenceException, InvalidArgumentException {
        List<Bill> bills = billDAO.findBillsInGroup(billQuery);

        for (Bill b : bills) {
            b.setBillShares(billShareService.getAllBillSharesForBill(b));
        }
        return bills;
    }

    @Override
    public List<Bill> searchBill(BillQuery query) {
        return null;
    }

    @Override
    public Bill findBillById(int billId) throws PersistenceException, InvalidArgumentException {
        if (billId < 1) throw new InvalidArgumentException("[Bill service] Bill ID invalid");
        return billDAO.findBillById(billId);
    }

    @Override
    public boolean validateBill(Bill bill) throws InvalidArgumentException {
        if (bill == null) {
            throw new InvalidArgumentException("Bill must not be null!");
        }
        StringBuilder message = new StringBuilder();
        String newLine = System.lineSeparator();
        if (bill.getAmount() == null) {
            message.append("Amount must not be null.").append(newLine);
        } else if (bill.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            message.append("Amount must not be < 0.");
        }
        if (bill.getTitle() == null || bill.getTitle().equals("")) {
            message.append("Title must not be empty.");
        }
        if (bill.getCurrency() == null) {
            message.append("Currency must be set.");
        }
        if (bill.getShareTechniqueId() == null) {
            message.append("Share technique must not be null.");
        }

        if (message.toString().equals(""))
            return true;
        else {
            throw new InvalidArgumentException(message.toString());
        }
    }
}
