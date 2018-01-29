package at.ac.tuwien.sepm.fridget.server.persistence.h2;

import at.ac.tuwien.sepm.fridget.common.entities.Bill;
import at.ac.tuwien.sepm.fridget.common.entities.BillShare;
import at.ac.tuwien.sepm.fridget.common.entities.User;
import at.ac.tuwien.sepm.fridget.common.exception.InvalidArgumentException;
import at.ac.tuwien.sepm.fridget.common.exception.PersistenceException;
import at.ac.tuwien.sepm.fridget.server.persistence.BillDAO;
import at.ac.tuwien.sepm.fridget.server.persistence.BillShareDAO;
import at.ac.tuwien.sepm.fridget.server.persistence.H2Util;
import at.ac.tuwien.sepm.fridget.server.persistence.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository("billShareDAO")
public class BillShareDAOH2 implements BillShareDAO {

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
    private BillDAO billDAO;

    @Autowired
    private UserDAO userDAO;

    /**
     * Table name
     */
    private static final String TABLE_NAME = "BillShare";

    private static final String DELETE_BILL_SHARES_FOR_BILL_ID_STATEMENT_SQL = "DELETE FROM BillShare WHERE bill_id = ?";

    /**
     * Fields of table
     */
    private static final String[] FIELDS = new String[] {"amount", "user_id", "bill_id"};

    private static final String[] ALLFIELDS = new String[] {"id", "amount", "user_id", "bill_id"};

    @Override
    public BillShare createBillShare(BillShare billShare, int billId) throws PersistenceException, InvalidArgumentException {
        if (billShare == null) {
            throw new InvalidArgumentException("BillShare must not be null!");
        }
        try {
            String sql = h2Util.generateInsertString(TABLE_NAME, FIELDS);

            PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            statement.setBigDecimal(1, billShare.getAmount());
            statement.setInt(2, billShare.getUser().getId());
            statement.setInt(3, billId);
            // Execute and retrieve the generated id
            statement.executeUpdate();

            // Update DTO
            billShare.setId(h2Util.getIdOfInserted(statement.getGeneratedKeys()));
            return billShare;
        } catch (SQLException e) {
            throw new PersistenceException("SQL exception thrown in create billshare DAO.", e);
        } catch (NullPointerException e) {
            throw new PersistenceException("NullPointerException thrown in billshare DAO.", e);
        }
    }

    @Override
    public BillShare editBillShare(BillShare billShare, int billId) throws InvalidArgumentException, PersistenceException {
        if (billShare == null)
            throw new InvalidArgumentException("BillShare must not be null");

        try {
            String sql = h2Util.generateUpdateString(TABLE_NAME, FIELDS, "ID = " + billShare.getId());
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setBigDecimal(1, billShare.getAmount());
            statement.setInt(2, billShare.getUser().getId());
            statement.setInt(3, billId);
            statement.executeUpdate();
            return billShare;
        } catch (SQLException e) {
            throw new PersistenceException("SQL Exception in editBillShare of BillShareDAO");
        }
    }

    @Override
    public void deleteBillShare(int id) throws InvalidArgumentException, PersistenceException {
        if (id < 0)
            throw new InvalidArgumentException("Id must not be negativ");

        try {
            String sql = h2Util.generateDeleteString(TABLE_NAME, "id=" + id);
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.execute();
        } catch (SQLException e) {
            throw new PersistenceException("SQL Exception in deleteBillShare of BillShareDAO");
        }
    }

    @Override
    public void deleteBillSharesForBillId(int billId) throws InvalidArgumentException, PersistenceException {
        try (PreparedStatement statement = connection.prepareStatement(DELETE_BILL_SHARES_FOR_BILL_ID_STATEMENT_SQL)) {
            statement.setInt(1, billId);
            statement.execute();
        } catch (SQLException e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public BillShare findBillShareById(int id) throws InvalidArgumentException, PersistenceException {
        if(id < 0)
            throw new InvalidArgumentException("BillId must not be negativ");

        try {
            String sql = h2Util.generateQueryString(TABLE_NAME, FIELDS, "id="+id);
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet rs = statement.executeQuery();
            BillShare billShare = null;

            if(rs.next()) {
                billShare = new BillShare();
                Bill bill = billDAO.findBillById(rs.getInt(ALLFIELDS[3]));
                billShare.setBill(bill);
                billShare.setId(id);
                billShare.setAmount(rs.getBigDecimal(ALLFIELDS[1]));

                User user = userDAO.findUserById(rs.getInt(ALLFIELDS[2]));
                billShare.setUser(user);
            }
            return billShare;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new PersistenceException("SQL Exception in findBillSharesByID of BillShareDAO");
        }
    }

    @Override
    public List<BillShare> getAllBillSharesForBill(Bill bill) throws InvalidArgumentException, PersistenceException {
        if (bill == null || bill.getId() < 1)
            throw new InvalidArgumentException("BillId must not be negativ");

        try {
            String sql = h2Util.generateQueryString(TABLE_NAME, ALLFIELDS, "bill_id =?");
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, bill.getId());
            ResultSet rs = statement.executeQuery();

            List<BillShare> billShares = new ArrayList<>();
            while (rs.next()) {
                BillShare billShare = new BillShare();
                billShare.setId(rs.getInt("id"));
                //billShare.setBill(bill);
                billShare.setUser(userDAO.findUserById(rs.getInt("user_id")));
                billShare.setAmount(rs.getBigDecimal("amount"));
                billShares.add(billShare);
            }
            return billShares;
        } catch (SQLException e) {
            throw new PersistenceException("SQL Exception in getAllBillSharesForBill of BillShareDAO");
        }
    }

    @Override
    public List<BillShare> getBillSharesForUser(List<Bill> bills, int userId) throws PersistenceException, InvalidArgumentException {
        if (bills == null) {
            throw new InvalidArgumentException("Bills must not be null");
        }
        try {
            List<BillShare> result = new ArrayList<>();
            for (Bill bill : bills) {
                String sql = h2Util.generateQueryString(TABLE_NAME, ALLFIELDS, "bill_id =? AND user_id =?");
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setInt(1, bill.getId());
                statement.setInt(2, userId);
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    BillShare billShare = new BillShare();
                    billShare.setId(resultSet.getInt(ALLFIELDS[0]));
                    billShare.setAmount(resultSet.getBigDecimal(ALLFIELDS[1]));
                    billShare.setId(resultSet.getInt(ALLFIELDS[2]));
                    billShare.setId(resultSet.getInt(ALLFIELDS[3]));
                    result.add(billShare);
                }
            }
            return result;
        } catch (SQLException e) {
            throw new PersistenceException("SQL Exception", e);
        }
    }

}
