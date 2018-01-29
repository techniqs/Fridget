package at.ac.tuwien.sepm.fridget.server.persistence.h2;

import at.ac.tuwien.sepm.fridget.common.entities.User;
import at.ac.tuwien.sepm.fridget.common.entities.UserCredentials;
import at.ac.tuwien.sepm.fridget.common.exception.InvalidArgumentException;
import at.ac.tuwien.sepm.fridget.common.exception.PersistenceException;
import at.ac.tuwien.sepm.fridget.common.util.PasswordHelpers;
import at.ac.tuwien.sepm.fridget.common.util.UserValidator;
import at.ac.tuwien.sepm.fridget.server.persistence.H2Util;
import at.ac.tuwien.sepm.fridget.server.persistence.UserDAO;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository("userDAO")
public class UserDAOH2 implements UserDAO {

    private static final String CREATE_USER_STATEMENT_SQL = "INSERT INTO User (name, email, password) VALUES (?, ?, ?)";
    private static final String RESET_PASSWORD_STATEMENT_SQL = "UPDATE User SET password = ? WHERE id = ?";
    private static final String EDIT_USER_STATEMENT_SQL = "UPDATE User SET name = ? WHERE id = ?";

    /**
     * Table name
     */
    private static final String TABLE_NAME = "User";

    /**
     * Fields of table
     */
    private static final String[] FIELDS = new String[] {"name", "email", "password"};

    /**
     * Database Util
     */
    private final H2Util h2Util;

    /**
     * Database connection
     */
    private final Connection connection;

    public UserDAOH2(H2Util h2Util, Connection connection) {
        this.h2Util = h2Util;
        this.connection = connection;
    }

    @Override
    public User createUser(User user) throws PersistenceException, InvalidArgumentException {
        UserValidator.getInstance().validateUserForRegistrationServer(user);
        try {
            PreparedStatement statement = connection.prepareStatement(CREATE_USER_STATEMENT_SQL, PreparedStatement.RETURN_GENERATED_KEYS);
            statement.setString(1, user.getName());
            statement.setString(2, user.getEmail());
            statement.setString(3, PasswordHelpers.hashForStorage(user.getPassword()));

            // Execute and retrieve the generated id
            statement.executeUpdate();

            // Update DTO
            user.setId(h2Util.getIdOfInserted(statement.getGeneratedKeys()));
            user.setPassword(null);
            return user;
        } catch (SQLException e) {
            throw new PersistenceException("SQL Exception thrown in Create User DAO.", e);
        }
    }

    @Override
    public User editUser(User user) throws PersistenceException, InvalidArgumentException {
        try (PreparedStatement statement = connection.prepareStatement(EDIT_USER_STATEMENT_SQL)) {
            statement.setString(1, user.getName());
            statement.setInt(2, user.getId());
            int updated = statement.executeUpdate();
            if (updated > 1) {
                throw new PersistenceException("Update modified too many rows");
            } else if (updated < 1) {
                throw new PersistenceException("Update modified no rows");
            }
            User result = findUserById(user.getId());
            result.setPassword(null);
            return result;
        } catch (SQLException e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public User findUserByCredentials(UserCredentials credentials) throws PersistenceException, InvalidArgumentException {
        if (credentials.getEmail() == null || credentials.getEmail().isEmpty()) {
            throw new InvalidArgumentException("Email missing");
        }
        if (credentials.getPassword() == null || credentials.getPassword().isEmpty()) {
            throw new InvalidArgumentException("Password missing");
        }

        try (PreparedStatement stmt = connection.prepareStatement("SELECT * FROM " + TABLE_NAME + " WHERE email = ?")) {
            stmt.setString(1, credentials.getEmail());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setName(rs.getString("name"));
                    user.setEmail(rs.getString("email"));
                    if (PasswordHelpers.compareWithDatabase(rs.getString("password"), credentials.getPassword())) {
                        return user;
                    } else {
                        return null;
                    }
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public User findUserById(int userId) throws PersistenceException {
        try (PreparedStatement stmt = connection.prepareStatement("SELECT * FROM " + TABLE_NAME + " WHERE id = ?")) {
            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setName(rs.getString("name"));
                    user.setEmail(rs.getString("email"));
                    return user;
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public User findUserByEmail(String userEmail) throws PersistenceException {
        try (PreparedStatement stmt = connection.prepareStatement("SELECT * FROM " + TABLE_NAME + " WHERE email = ?")) {
            stmt.setString(1, userEmail);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setName(rs.getString("name"));
                    user.setEmail(rs.getString("email"));
                    return user;
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public void editPassword(User user) throws PersistenceException {
        try (PreparedStatement statement = connection.prepareStatement(RESET_PASSWORD_STATEMENT_SQL)) {
            statement.setString(1, PasswordHelpers.hashForStorage(user.getPassword()));
            statement.setInt(2, user.getId());
            int updated = statement.executeUpdate();
            if (updated > 1) {
                throw new PersistenceException("Update modified too many rows");
            } else if (updated < 1) {
                throw new PersistenceException("Update modified no rows");
            }
        } catch (SQLException e) {
            throw new PersistenceException(e);
        }
    }

}
