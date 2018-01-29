package at.ac.tuwien.sepm.fridget.server.persistence.h2;

import at.ac.tuwien.sepm.fridget.common.entities.Transaction;
import at.ac.tuwien.sepm.fridget.common.exception.PersistenceException;
import at.ac.tuwien.sepm.fridget.server.persistence.TransactionDAO;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository("TransactionDAO")
public class TransactionDAOH2 implements TransactionDAO {

    /** Table name */
    private static final String TABLE_NAME = "Transaction";

    /** Fields of table */
    private static final String[] FIELDS = new String[] {"id", "created_at", "amount", "bill_id", "user_paid_id", "user_received_id"};

    private static final String CREATE_TRANSACTION_STATEMENT_SQL = "INSERT INTO Transaction (amount, bill_id, user_paid_id, user_received_id) VALUES (?, ?, ?, ?)";
    private static final String DELETE_TRANSACTIONS_STATEMENT_SQL = "DELETE FROM Transaction WHERE bill_id = ?";

    private static final String GET_SUM_PAID_STATEMENT_SQL = "SELECT COALESCE(SUM(Transaction.amount), 0) AS paid FROM Transaction LEFT JOIN Bill ON (bill_id = Bill.id) WHERE group_id = ? AND user_paid_id = ?";
    private static final String GET_SUM_RECEIVED_STATEMENT_SQL = "SELECT COALESCE(SUM(Transaction.amount), 0) AS received FROM Transaction LEFT JOIN Bill ON (bill_id = Bill.id) WHERE group_id = ? AND user_received_id = ?";
    private static final String GET_PAID_TO_MEMBERS_STATEMENT_SQL = "SELECT user_received_id, SUM(Transaction.amount) AS balance FROM Transaction LEFT JOIN Bill ON (bill_id = Bill.id) WHERE group_id = ? AND user_paid_id = ? GROUP BY user_received_id";
    private static final String GET_RECEIVED_BY_MEMBERS_STATEMENT_SQL = "SELECT user_paid_id, SUM(Transaction.amount) AS balance FROM Transaction LEFT JOIN Bill ON (bill_id = Bill.id) WHERE group_id = ? AND user_received_id = ? GROUP BY user_paid_id";

    /** Database connection */
    private final Connection connection;

    public TransactionDAOH2(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void createTransactions(List<Transaction> transactions) throws PersistenceException {
        try (PreparedStatement statement = connection.prepareStatement(CREATE_TRANSACTION_STATEMENT_SQL)) {
            for (Transaction transaction : transactions) {
                statement.setBigDecimal(1, transaction.getAmount());
                statement.setInt(2, transaction.getBillId());
                statement.setInt(3, transaction.getUserPaidId());
                statement.setInt(4, transaction.getUserReceivedId());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public void deleteTransactionsForBillId(int billId) throws PersistenceException {
        try (PreparedStatement statement = connection.prepareStatement(DELETE_TRANSACTIONS_STATEMENT_SQL)) {
            statement.setInt(1, billId);
            statement.execute();
        } catch (SQLException e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public BigDecimal getBalance(int groupId, int userId) throws PersistenceException {
        BigDecimal balance = BigDecimal.ZERO;

        // When you pay for someone, this affects your balance positively (they have debt to you):
        balance = balance.add(queryBigDecimal(GET_SUM_PAID_STATEMENT_SQL, groupId, userId));

        // When someone pays for you, this affects your balance negatively (you are in debt):
        balance = balance.subtract(queryBigDecimal(GET_SUM_RECEIVED_STATEMENT_SQL, groupId, userId));

        return balance;
    }

    @Override
    public Map<Integer, BigDecimal> getBalanceToMembers(int groupId, int userId) throws PersistenceException {
        // Take all outgoing transactions as basis:
        Map<Integer, BigDecimal> result = queryBigDecimalMap(GET_PAID_TO_MEMBERS_STATEMENT_SQL, groupId, userId);

        // Subtract the sums that others have paid for you:
        Map<Integer, BigDecimal> receivedMap = queryBigDecimalMap(GET_RECEIVED_BY_MEMBERS_STATEMENT_SQL, groupId, userId);
        for (Map.Entry<Integer, BigDecimal> entry : receivedMap.entrySet()) {
            result.put(entry.getKey(), result.getOrDefault(entry.getKey(), BigDecimal.ZERO).subtract(entry.getValue()));
        }

        return result;
    }


    private Map<Integer, BigDecimal> queryBigDecimalMap(String query, int groupId, int userId) throws PersistenceException {
        Map<Integer, BigDecimal> result = new HashMap<>();
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, groupId);
            statement.setInt(2, userId);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    result.put(rs.getInt(1), rs.getBigDecimal(2));
                }
            }
        } catch (SQLException e) {
            throw new PersistenceException(e);
        }
        return result;
    }

    private BigDecimal queryBigDecimal(String query, int groupId, int userId) throws PersistenceException {
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, groupId);
            statement.setInt(2, userId);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal(1);
                } else {
                    return BigDecimal.ZERO;
                }
            }
        } catch (SQLException e) {
            throw new PersistenceException(e);
        }
    }

}

