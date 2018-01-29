package at.ac.tuwien.sepm.fridget.server.persistence;

import at.ac.tuwien.sepm.fridget.common.entities.Bill;
import at.ac.tuwien.sepm.fridget.common.entities.BillQuery;
import at.ac.tuwien.sepm.fridget.common.exception.InvalidArgumentException;
import at.ac.tuwien.sepm.fridget.common.exception.PersistenceException;

import java.util.List;

public interface BillDAO {

    Bill createBill(Bill bill) throws PersistenceException, InvalidArgumentException;

    Bill editBill(Bill bill) throws InvalidArgumentException, PersistenceException;

    /**
     * @param billId ID of the bill to be deleted
     * @return true - deletion worked; false - otherwise
     */
    boolean deleteBill(int billId) throws PersistenceException, InvalidArgumentException;

    /**
     * @param billId ID of the restored bill
     * @return true - undo deletion worked; false - otherwise
     */
    boolean undoDeleteBill(int billId) throws PersistenceException, InvalidArgumentException;

    /**
     * @param id of the bill to be find
     * @return bill if it exists otherwise null
     */
    Bill findBillById(int id) throws InvalidArgumentException, PersistenceException;

    /**
     * Find bills matching the filters of billQuery
     *
     * @param billQuery filter information
     * @return a list of all bills matching the filters
     * @throws InvalidArgumentException
     * @throws PersistenceException     Thrown on internal errors of the persistence layer
     */
    List<Bill> findBillsInGroup(BillQuery billQuery) throws InvalidArgumentException, PersistenceException;

    List<Bill> getAllBillsForGroup(int groupId) throws PersistenceException, InvalidArgumentException;
}
