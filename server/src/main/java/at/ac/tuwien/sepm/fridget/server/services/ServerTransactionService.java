package at.ac.tuwien.sepm.fridget.server.services;

import at.ac.tuwien.sepm.fridget.common.exception.InvalidArgumentException;
import at.ac.tuwien.sepm.fridget.common.exception.PersistenceException;
import at.ac.tuwien.sepm.fridget.common.services.TransactionService;
import at.ac.tuwien.sepm.fridget.server.persistence.TransactionDAO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Service("transactionService")
public class ServerTransactionService implements TransactionService {

    private final TransactionDAO transactionDAO;


    public ServerTransactionService(TransactionDAO transactionDAO) {
        this.transactionDAO = transactionDAO;
    }


    @Override
    public BigDecimal getBalance(int groupId, int userId) throws PersistenceException, InvalidArgumentException {
        return transactionDAO.getBalance(groupId, userId);
    }

    @Override
    public Map<Integer, BigDecimal> getBalanceToMembers(int groupId, int userId) throws PersistenceException, InvalidArgumentException {
        return transactionDAO.getBalanceToMembers(groupId, userId);
    }

}
