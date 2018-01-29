package at.ac.tuwien.sepm.fridget.common.services;

import at.ac.tuwien.sepm.fridget.common.entities.Bill;
import at.ac.tuwien.sepm.fridget.common.entities.BillQuery;
import at.ac.tuwien.sepm.fridget.common.entities.User;
import at.ac.tuwien.sepm.fridget.common.exception.InvalidArgumentException;
import at.ac.tuwien.sepm.fridget.common.exception.PersistenceException;

import java.util.List;

public interface BillService {

    /**
     * 3.6 Add Multiple Currencies!
     * 10.2 Create with Bill Template!
     * Bill with Tag
     * 10.1 Get Sum of Invoice by scanning it!
     */
    Bill createBill(Bill bill, User user) throws PersistenceException, InvalidArgumentException;

    Bill editBill(Bill editedBill) throws PersistenceException, InvalidArgumentException;

    boolean deleteBill(int billID) throws InvalidArgumentException, PersistenceException;

    boolean undoDeleteBill(int billID) throws InvalidArgumentException, PersistenceException;

    //boolean splitBill(List<User> users,int splitMethod);

    /**
     * returns a list with bills of a group and time range ordered by date
     * @param billQuery holding all filters (minDate and maxDate must be set)
     * @return list with bills matching the filters
     * @throws PersistenceException     Thrown on internal errors of the persistence layer
     * @throws InvalidArgumentException Thrown on invalid arguments
     */
    List<Bill> listAllBills(BillQuery billQuery) throws PersistenceException, InvalidArgumentException;

    /**
     * //9.2 Show bills with specific Filters
     * Filter by entering a date range
     */
    List<Bill> searchBill(BillQuery query);

    /**
     * Gets a bill by its id
     *
     * @param billId the id of the bill
     * @return the retrieved bill
     * @throws PersistenceException
     * @throws InvalidArgumentException
     */
    Bill findBillById(int billId) throws PersistenceException, InvalidArgumentException;

    /**
     * Bill validation
     *
     * @param bill the bill to validate
     * @return true if valid
     * @throws IllegalArgumentException otherwise. Message contains the invalid fields
     */
    boolean validateBill(Bill bill) throws InvalidArgumentException;

}
