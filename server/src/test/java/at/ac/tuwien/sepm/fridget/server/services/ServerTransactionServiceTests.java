package at.ac.tuwien.sepm.fridget.server.services;

import at.ac.tuwien.sepm.fridget.common.exception.InvalidArgumentException;
import at.ac.tuwien.sepm.fridget.common.exception.PersistenceException;
import at.ac.tuwien.sepm.fridget.common.services.TransactionService;
import at.ac.tuwien.sepm.fridget.server.TestBase;
import at.ac.tuwien.sepm.fridget.server.persistence.TransactionDAO;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class ServerTransactionServiceTests extends TestBase {

    @Test
    public void getBalance_ShouldCallDAO() throws InvalidArgumentException, PersistenceException {
        TransactionDAO transactionDAO = mock(TransactionDAO.class);
        TransactionService transactionService = new ServerTransactionService(transactionDAO);
        when(transactionDAO.getBalance(anyInt(), anyInt())).thenReturn(new BigDecimal(789));
        assertThat(transactionService.getBalance(123, 456)).isEqualTo(new BigDecimal(789));
        verify(transactionDAO).getBalance(123, 456);
    }

    @Test
    public void getBalanceToMembers_ShouldCallDAO() throws InvalidArgumentException, PersistenceException {
        TransactionDAO transactionDAO = mock(TransactionDAO.class);
        TransactionService transactionService = new ServerTransactionService(transactionDAO);

        Map<Integer, BigDecimal> result = new HashMap<>();
        result.put(222, new BigDecimal(789));

        when(transactionDAO.getBalanceToMembers(anyInt(), anyInt())).thenReturn(result);
        assertThat(transactionService.getBalanceToMembers(123, 456)).isEqualTo(result);
        verify(transactionDAO).getBalanceToMembers(123, 456);
    }

}
