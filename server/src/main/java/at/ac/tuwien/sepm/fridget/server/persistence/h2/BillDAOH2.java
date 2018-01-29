package at.ac.tuwien.sepm.fridget.server.persistence.h2;

import at.ac.tuwien.sepm.fridget.common.entities.*;
import at.ac.tuwien.sepm.fridget.common.exception.InvalidArgumentException;
import at.ac.tuwien.sepm.fridget.common.exception.PersistenceException;
import at.ac.tuwien.sepm.fridget.common.util.Currency;
import at.ac.tuwien.sepm.fridget.common.util.ShareTechniqueId;
import at.ac.tuwien.sepm.fridget.server.persistence.BillDAO;
import at.ac.tuwien.sepm.fridget.server.persistence.BillShareDAO;
import at.ac.tuwien.sepm.fridget.server.persistence.GroupDAO;
import at.ac.tuwien.sepm.fridget.server.persistence.H2Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Repository("billDAO")
public class BillDAOH2 implements BillDAO {

    /**
     * Database Util
     */
    @Autowired
    H2Util h2Util;

    /**
     * Database connection
     */
    @Autowired
    private Connection connection;

    @Autowired
    @Lazy
    private BillShareDAO billShareDAO;

    @Autowired
    private GroupDAO groupDAO;

    /**
     * All Fields of Table Bill
     */
    private static final String[] ALLFIELDS = new String[] {"id", "amount", "share_technique", "title", "description",
        "created_at", "updated_at", "deleted", "currency", "exchange_rate", "user_id", "group_id", "tag_id"};

    /**
     * Table name
     */
    private static final String TABLE_NAME = "Bill";

    /**
     * Fields of table
     */
    private static final String[] FIELDS = new String[] {"amount", "share_technique", "title", "description",
        "created_at", "updated_at", "deleted", "currency", "exchange_rate", "user_id", "group_id", "tag_id"};



    public BillDAOH2() {
    }

    @Override
    public Bill createBill(Bill bill) throws PersistenceException, InvalidArgumentException {
        if (bill == null) {
            throw new InvalidArgumentException("Bill must not be null!");
        }
        try {
            String sql = h2Util.generateInsertString(TABLE_NAME, FIELDS);

            PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            statement.setBigDecimal(1, bill.getAmount());
            statement.setInt(2, bill.getShareTechniqueId().getId());
            statement.setString(3, bill.getTitle());
            statement.setString(4, bill.getDescription());
            statement.setTimestamp(5, Timestamp.from(Instant.now()));
            statement.setTimestamp(6, Timestamp.from(Instant.now()));
            statement.setBoolean(7, bill.isDeleted());
            statement.setInt(8, bill.getCurrency().getId());
            statement.setBigDecimal(9, bill.getExchangeRate());
            statement.setInt(10, bill.getUser().getId());
            statement.setInt(11, bill.getGroup().getId());
            if (bill.getTag() != null) {
                statement.setInt(12, bill.getTag().getId());
            } else {
                statement.setNull(12, Types.INTEGER);
            }

            // Execute and retrieve the generated id
            statement.executeUpdate();

            // Update DTO
            bill.setId(h2Util.getIdOfInserted(statement.getGeneratedKeys()));
            return bill;
        } catch (SQLException e) {
            throw new PersistenceException("SQL Exception thrown in Create Bill DAO.", e);
        }
    }

    @Override
    public Bill editBill(Bill bill) throws InvalidArgumentException, PersistenceException {
        if (bill == null)
            throw new InvalidArgumentException("Bill must not be null!");

        try {
            PreparedStatement statement = connection.prepareStatement(h2Util.generateUpdateString(TABLE_NAME, FIELDS, "ID = " + bill.getId()));
            statement.setBigDecimal(1, bill.getAmount());
            statement.setInt(2, bill.getShareTechniqueId().getId());
            statement.setString(3, bill.getTitle());
            statement.setString(4, bill.getDescription());
            statement.setTimestamp(5, Timestamp.valueOf(bill.getCreatedAt()));
            statement.setTimestamp(6, Timestamp.from(Instant.now()));
            statement.setBoolean(7, bill.isDeleted());
            statement.setInt(8, bill.getCurrency().getId());
            statement.setBigDecimal(9, bill.getExchangeRate());
            statement.setInt(10, bill.getUser().getId());
            statement.setInt(11, bill.getGroup().getId());
            if (bill.getTag() != null) {
                statement.setInt(12, bill.getTag().getId());
            } else {
                statement.setNull(12, Types.INTEGER);
            }

            statement.executeUpdate();
            return bill;
        } catch (SQLException e) {
            throw new PersistenceException("SQL Exception thrown in Edit Bill DAO");
        }
    }

    @Override
    public boolean deleteBill(int billId) throws PersistenceException, InvalidArgumentException {
        if (billId < 0)
            throw new InvalidArgumentException("Bill id must not be negativ!");

        try {
            PreparedStatement statement = connection.prepareStatement("UPDATE BILL SET DELETED=TRUE WHERE ID=?");
            statement.setInt(1, billId);
            return statement.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new PersistenceException("SQL Exception thrown in deleteBill in BillDAO", e);
        }
    }

    @Override
    public boolean undoDeleteBill(int billId) throws PersistenceException, InvalidArgumentException {
        if (billId < 0)
            throw new InvalidArgumentException("Bill id must not be negativ!");

        try {
            PreparedStatement statement = connection.prepareStatement("UPDATE BILL SET DELETED = FALSE WHERE ID=?");
            statement.setInt(1, billId);
            return statement.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new PersistenceException("SQL Exception thrown in undoDeleteBill in BillDAO", e);
        }
    }

    @Override
    public Bill findBillById(int id) throws InvalidArgumentException, PersistenceException {
        if (id < 1)
            throw new InvalidArgumentException("Bill id must not be negative!");
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM BILL B LEFT OUTER JOIN TAG T ON B.TAG_ID = T.ID WHERE B.ID=?");
            statement.setInt(1, id);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                Group group = groupDAO.getGroup(rs.getInt("GROUP_ID"));
                Bill bill = h2Util.getBillOfResultSet(rs);
                bill.setGroup(group);
                bill.setBillShares(billShareDAO.getAllBillSharesForBill(bill));
                return bill;
            }else
                throw new InvalidArgumentException("No Bill with ID: " + id + " exists in database");
        } catch (SQLException e) {
            throw new PersistenceException("SQL Exception thrown in findBillById in BillDAO", e);
        }
    }


    public List<Bill> findBillsInGroup(BillQuery billQuery) throws InvalidArgumentException, PersistenceException {
        if (billQuery == null) {
            throw new InvalidArgumentException("BillQuery must not be null!");
        } else if (billQuery.getGroup() == null)
            throw new InvalidArgumentException("Group of BillQuery must be set!");

        try {
            String sql = "SELECT * FROM BILL B LEFT OUTER JOIN TAG T ON B.TAG_ID = T.ID WHERE B.GROUP_ID=? ";
            sql += (billQuery.getMinDate() != null) ? " AND CREATED_AT >= ?" : "";
            sql += (billQuery.getMaxDate() != null) ? " AND CREATED_AT <= ?" : "";
            sql += (billQuery.getCurrency() != null) ? " AND CURRENCY = ?" : "";
            sql += (billQuery.getText() != null) ? " AND DESCRIPTION LIKE '%" + billQuery.getText() + "%'" : "";
            sql += (billQuery.getMaxAmount() != null) ? " AND AMOUNT <= ? " : "";
            sql += (billQuery.getMinAmount() != null) ? " AND AMOUNT >= ? " : "";
            sql += (billQuery.getPayer() != null) ? " AND USER_ID = ? " : "";
            sql += (billQuery.getTag() != null) ? " AND TAG_ID = ? " : "";
            sql += (billQuery.isDeleted() ^ billQuery.isValid() ? " AND DELETED = ? " : "");

            PreparedStatement stmt_bills = connection.prepareStatement(sql);
            stmt_bills.setInt(1, billQuery.getGroup().getId());

            int i = 2;
            if (billQuery.getMinDate() != null)
                stmt_bills.setTimestamp(i++, Timestamp.valueOf(billQuery.getMinDate()));
            if (billQuery.getMaxDate() != null)
                stmt_bills.setTimestamp(i++, Timestamp.valueOf(billQuery.getMaxDate()));
            if (billQuery.getCurrency() != null)
                stmt_bills.setInt(i++, billQuery.getCurrency().getId());
            if (billQuery.getMaxAmount() != null)
                stmt_bills.setBigDecimal(i++, billQuery.getMaxAmount());
            if (billQuery.getMinAmount() != null)
                stmt_bills.setBigDecimal(i++, billQuery.getMinAmount());
            if (billQuery.getPayer() != null)
                stmt_bills.setInt(i++, billQuery.getPayer().getId());
            if (billQuery.getTag() != null)
                stmt_bills.setInt(i++, billQuery.getTag().getId());
            if (!billQuery.isDeleted() && billQuery.isValid())
                stmt_bills.setBoolean(i++, !billQuery.isValid());
            if (billQuery.isDeleted() && !billQuery.isValid())
                stmt_bills.setBoolean(i++, billQuery.isDeleted());

            ResultSet rs = stmt_bills.executeQuery();
            List<Bill> bills = new LinkedList<>();

            while (rs.next()) {
                Bill b = h2Util.getBillOfResultSet(rs);
                bills.add(b);
            }

            return bills;
        } catch (SQLException e) {
            throw new PersistenceException("SQL Exception thrown in findBillsInGroup in BillDAO", e);
        }
    }

    @Override
    public List<Bill> getAllBillsForGroup(int groupId) throws PersistenceException, InvalidArgumentException {
        try {
            String sql = h2Util.generateQueryString(TABLE_NAME, ALLFIELDS, "group_id = ?");
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, groupId);
            ResultSet resultSet = statement.executeQuery();
            List<Bill> result = new ArrayList<>();
            while (resultSet.next()) {
                Bill bill = new Bill();
                bill.setId(resultSet.getInt(ALLFIELDS[0]));
                bill.setAmount(resultSet.getBigDecimal(ALLFIELDS[1]));
                bill.setShareTechniqueId(ShareTechniqueId.fromId(resultSet.getInt(ALLFIELDS[2])));
                bill.setTitle(resultSet.getString(ALLFIELDS[3]));
                bill.setDescription(resultSet.getString(ALLFIELDS[4]));
                bill.setCreatedAt(resultSet.getTimestamp(ALLFIELDS[5]).toLocalDateTime());
                bill.setUpdatedAt(resultSet.getTimestamp(ALLFIELDS[6]).toLocalDateTime());
                bill.setDeleted(resultSet.getBoolean(ALLFIELDS[7]));
                bill.setCurrency(Currency.fromId(resultSet.getInt(ALLFIELDS[8])));
                bill.setExchangeRate(resultSet.getBigDecimal(ALLFIELDS[9]));
                bill.setUser(new User(resultSet.getInt(ALLFIELDS[10])));
                bill.setTag(new Tag(resultSet.getInt(ALLFIELDS[11])));
                bill.setGroup(new Group(groupId));
                bill.setBillShares(billShareDAO.getAllBillSharesForBill(bill));
                result.add(bill);
            }
            return result;

        } catch (SQLException e) {
            throw new PersistenceException("SQL Exception", e);
        }
    }
}