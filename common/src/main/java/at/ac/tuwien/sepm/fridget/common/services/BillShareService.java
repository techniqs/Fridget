package at.ac.tuwien.sepm.fridget.common.services;

import at.ac.tuwien.sepm.fridget.common.entities.Bill;
import at.ac.tuwien.sepm.fridget.common.entities.BillShare;
import at.ac.tuwien.sepm.fridget.common.exception.InvalidArgumentException;
import at.ac.tuwien.sepm.fridget.common.exception.PersistenceException;

import java.util.List;

public interface BillShareService {

    BillShare createBillShare(BillShare billShare, int billId) throws PersistenceException, InvalidArgumentException;

    BillShare editBillShare(BillShare billShare, int billId) throws PersistenceException, InvalidArgumentException;

    void deleteBillShare(int id) throws PersistenceException, InvalidArgumentException;

    void deleteBillSharesForBillId(int billId) throws PersistenceException, InvalidArgumentException;

    BillShare readBillShare(int id) throws PersistenceException, InvalidArgumentException;

    /**
     * Get all BillShares for a specific Bill
     *
     * @param bill the specific bill
     * @return List of Billshares
     * @throws PersistenceException Thrown on internal errors of the persistence layer
     * @throws InvalidArgumentException Thrown on invalid arguments
     */
    List<BillShare> getAllBillSharesForBill(Bill bill) throws PersistenceException, InvalidArgumentException;

}
