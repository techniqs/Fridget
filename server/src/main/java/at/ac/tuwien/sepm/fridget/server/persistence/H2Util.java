package at.ac.tuwien.sepm.fridget.server.persistence;

import at.ac.tuwien.sepm.fridget.common.entities.Bill;
import at.ac.tuwien.sepm.fridget.common.entities.Tag;
import at.ac.tuwien.sepm.fridget.common.exception.InvalidArgumentException;
import at.ac.tuwien.sepm.fridget.common.exception.PersistenceException;
import at.ac.tuwien.sepm.fridget.common.util.Currency;
import at.ac.tuwien.sepm.fridget.common.util.ShareTechniqueId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Singleton helper class for handling the connection to the H2 database
 *
 * @author Max Graf
 */
@Component
public class H2Util {

    @Autowired
    private UserDAO userDAO;
    @Autowired
    private GroupDAO groupDAO;

    /**
     * Helper method to insert data into table
     *
     * @param table  the name of the table, e.g. "Bill"
     * @param fields the fields of the table, e.g. ["amount", "share_technique", "title", "description",
    "created_at", "updated_at", "deleted", "currency", "exchange_rate"]
     * @return an syntactically correct SQL string for inserting data
     */
    public String generateInsertString(String table, String[] fields) {

        // Number of values for generating placeholder question marks
        int numberOfValues = fields.length;

        // Generate placeholder question marks for SQL string
        String placeholders = "";
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(placeholders);

        for (String field : fields) {
            stringBuilder.append("?, ");
        }
        placeholders = stringBuilder.toString();

        // Remove last comma and space
        if (numberOfValues > 0) {
            placeholders = placeholders.substring(0, placeholders.length() - 2);
        }

        return "INSERT INTO " + table + "(" + String.join(",", fields) + ") VALUES (" + placeholders + ")";
    }

    public String generateUpdateString(String table, String[] fields, String whereClause) {
        StringBuilder query = new StringBuilder("UPDATE " + table + " SET ");

        // Concat fields with placeholders
        for (String field : fields) {
            query.append(field);
            query.append(" = ?, ");
        }

        // Remove last space and comma
        query.deleteCharAt(query.length() - 1);
        query.deleteCharAt(query.length() - 1);

        // Attach where clause
        query.append(" WHERE ");
        query.append(whereClause);

        return query.toString();
    }

    public String generateQueryString(String table, String[] fields, String whereClause) {
        return "SELECT " + String.join(",", fields) + " FROM " + table + " WHERE " + whereClause;
    }

    public String generateDeleteString(String table, String whereClause) {
        return "DELETE FROM " + table + " WHERE " + whereClause;
    }

    /**
     * Convenience method to get the id of newly inserted item from db
     *
     * @param resultSet the resultset of the query
     * @return the generated ID
     * @throws SQLException if something goes wrong during retrieval
     */
    public int getIdOfInserted(ResultSet resultSet) throws SQLException {
        if (resultSet.next()) {
            return resultSet.getInt(1);
        }
        return -1;
    }

    /**
     * Helper method to set parameters of bill from resultset
     *
     * @param resultSet of the query
     * @return a Bill where ID, AMOUNT, SHARE_TECHNIQUE, TITLE, DESCRIPTION, CREATED_AT, UPDATED_AT, CURRENCY, EXCHANGE_RATE, USER is set
     * @throws SQLException if something goes wrong during retrieval
     */
    public Bill getBillOfResultSet(ResultSet resultSet) throws SQLException, PersistenceException, InvalidArgumentException {
        Bill bill = new Bill();
        Tag tag = new Tag();
        tag.setId(resultSet.getInt("TAG.ID"));
        tag.setName(resultSet.getString("TAG.NAME"));

        bill.setId(resultSet.getInt("ID"));
        bill.setAmount(resultSet.getBigDecimal("AMOUNT"));
        bill.setShareTechniqueId(ShareTechniqueId.fromId(resultSet.getInt("SHARE_TECHNIQUE")));
        bill.setTitle(resultSet.getString("TITLE"));
        bill.setDescription(resultSet.getString("DESCRIPTION"));
        bill.setCreatedAt(resultSet.getTimestamp("CREATED_AT").toLocalDateTime());
        bill.setUpdatedAt(resultSet.getTimestamp("UPDATED_AT").toLocalDateTime());
        bill.setDeleted(resultSet.getBoolean("DELETED"));
        bill.setCurrency(Currency.fromId(resultSet.getInt("CURRENCY")));
        bill.setExchangeRate(resultSet.getBigDecimal("EXCHANGE_RATE"));

        bill.setTag(tag);
        bill.setUser(userDAO.findUserById(resultSet.getInt("USER_ID")));
        bill.setGroup(groupDAO.getGroup(resultSet.getInt("GROUP_ID")));

        return bill;
    }
}
