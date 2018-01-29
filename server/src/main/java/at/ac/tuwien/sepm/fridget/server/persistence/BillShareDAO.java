package at.ac.tuwien.sepm.fridget.server.persistence;

import at.ac.tuwien.sepm.fridget.common.entities.Bill;
import at.ac.tuwien.sepm.fridget.common.entities.BillShare;
import at.ac.tuwien.sepm.fridget.common.exception.InvalidArgumentException;
import at.ac.tuwien.sepm.fridget.common.exception.PersistenceException;

import java.util.List;

public interface BillShareDAO {
    BillShare createBillShare(BillShare billShare, int billId) throws PersistenceException, InvalidArgumentException;

    BillShare editBillShare(BillShare billShare, int billId) throws InvalidArgumentException, PersistenceException;

    void deleteBillShare(int billShareId) throws InvalidArgumentException, PersistenceException;

    void deleteBillSharesForBillId(int billId) throws InvalidArgumentException, PersistenceException;

    BillShare findBillShareById(int id) throws InvalidArgumentException, PersistenceException;

    /**
     * Get All Billshares for specific Bill
     *
     * @param bill the bill
     * @return List of Billshares
     * @throws PersistenceException Thrown on internal errors of the persistence layer
     */
    List<BillShare> getAllBillSharesForBill(Bill bill) throws PersistenceException, InvalidArgumentException;

    /**
     * Get All Billshares for User
     *
     * @param bills  All bills of a group
     * @param userId Id of the User
     * @return List of Billshares
     * @throws PersistenceException     Thrown on internal errors of the persistence layer
     * @throws InvalidArgumentException Thrown on invalid arguments
     */
    List<BillShare> getBillSharesForUser(List<Bill> bills, int userId) throws PersistenceException, InvalidArgumentException;

}
