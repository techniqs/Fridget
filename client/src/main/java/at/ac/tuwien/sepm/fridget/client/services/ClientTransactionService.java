package at.ac.tuwien.sepm.fridget.client.services;

import at.ac.tuwien.sepm.fridget.client.util.RestClient;
import at.ac.tuwien.sepm.fridget.common.entities.BalanceToMembers;
import at.ac.tuwien.sepm.fridget.common.exception.PersistenceException;
import at.ac.tuwien.sepm.fridget.common.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class ClientTransactionService implements TransactionService {

    @Autowired
    private RestClient restClient;

    @Override
    public BigDecimal getBalance(int groupId, int userId) throws PersistenceException {
        try {
            return restClient.postForObject("/transaction/getBalance", groupId, BigDecimal.class);
        } catch (Exception e) {
            throw new PersistenceException("Internal Server Error", e);
        }
    }

    @Override
    public Map<Integer, BigDecimal> getBalanceToMembers(int groupId, int userId) throws PersistenceException {
        try {
            return restClient.postForObject("/transaction/getBalanceToMembers", groupId, BalanceToMembers.class).getBalances();
        } catch (Exception e) {
            throw new PersistenceException("Internal Server Error", e);
        }
    }

}
