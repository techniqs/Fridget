package at.ac.tuwien.sepm.fridget.common.services;

import at.ac.tuwien.sepm.fridget.common.exception.InvalidArgumentException;
import at.ac.tuwien.sepm.fridget.common.exception.PersistenceException;

import java.math.BigDecimal;
import java.util.Map;

public interface TransactionService {

    /**
     * Gets the Balance for a user of a group
     *
     * @param groupId Id of the Group
     * @param userId  Id of the User
     * @return Balance of the User
     * @throws PersistenceException     Thrown on internal errors of the persistence layer
     * @throws InvalidArgumentException Thrown on invalid arguments
     */
    BigDecimal getBalance(int groupId, int userId) throws PersistenceException, InvalidArgumentException;

    Map<Integer, BigDecimal> getBalanceToMembers(int groupId, int userId) throws PersistenceException, InvalidArgumentException;

}
