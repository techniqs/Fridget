package at.ac.tuwien.sepm.fridget.server.persistence;

import at.ac.tuwien.sepm.fridget.common.entities.Transaction;
import at.ac.tuwien.sepm.fridget.common.exception.PersistenceException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface TransactionDAO {

    void createTransactions(List<Transaction> transactions) throws PersistenceException;

    void deleteTransactionsForBillId(int billId) throws PersistenceException;

    BigDecimal getBalance(int groupId, int userId) throws PersistenceException;

    Map<Integer, BigDecimal> getBalanceToMembers(int groupId, int userId) throws PersistenceException;

}
