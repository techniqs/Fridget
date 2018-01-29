package at.ac.tuwien.sepm.fridget.server.persistence.h2;

import at.ac.tuwien.sepm.fridget.common.entities.PasswordResetCode;
import at.ac.tuwien.sepm.fridget.common.exception.InvalidArgumentException;
import at.ac.tuwien.sepm.fridget.common.exception.PersistenceException;
import at.ac.tuwien.sepm.fridget.server.persistence.PasswordResetCodeDAO;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

@Repository("passwordResetCodeDAO")
public class PasswordResetCodeDAOH2 implements PasswordResetCodeDAO {

    private static final String CREATE_STATEMENT_SQL = "INSERT INTO PasswordResetCode (user_id, code) VALUES (?, ?)";
    private static final String VERIFY_STATEMENT_SQL = "SELECT * FROM PasswordResetCode WHERE user_id = ? AND code = ?";
    private static final String DELETE_STATEMENT_SQL = "DELETE FROM PasswordResetCode WHERE user_id = ? AND code = ?";
    private static final int PASSWORD_RESET_EXPIRATION_HOURS = 24;

    private final Connection connection;

    public PasswordResetCodeDAOH2(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void create(PasswordResetCode code) throws PersistenceException, InvalidArgumentException {
        if (code == null) throw new InvalidArgumentException("PasswordResetCode is required");
        if (code.getUser() == null) throw new InvalidArgumentException("User is required");
        if (code.getCode() == null || code.getCode().isEmpty()) throw new InvalidArgumentException("Code is required");

        try (PreparedStatement statement = connection.prepareStatement(CREATE_STATEMENT_SQL)) {
            statement.setInt(1, code.getUser().getId());
            statement.setString(2, code.getCode());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public boolean verify(PasswordResetCode code) throws PersistenceException, InvalidArgumentException {
        if (code == null) throw new InvalidArgumentException("PasswordResetCode is required");
        if (code.getUser() == null) throw new InvalidArgumentException("User is required");
        if (code.getCode() == null || code.getCode().isEmpty()) throw new InvalidArgumentException("Code is required");

        try (PreparedStatement statement = connection.prepareStatement(VERIFY_STATEMENT_SQL)) {
            statement.setInt(1, code.getUser().getId());
            statement.setString(2, code.getCode());
            try (ResultSet rs = statement.executeQuery()) {
                if (!rs.next()) return false;
                LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();
                return createdAt.isAfter(LocalDateTime.now().minusHours(PASSWORD_RESET_EXPIRATION_HOURS));
            }
        } catch (SQLException e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public void delete(PasswordResetCode code) throws PersistenceException, InvalidArgumentException {
        if (code == null) throw new InvalidArgumentException("PasswordResetCode is required");
        if (code.getUser() == null) throw new InvalidArgumentException("User is required");
        if (code.getCode() == null || code.getCode().isEmpty()) throw new InvalidArgumentException("Code is required");

        try (PreparedStatement statement = connection.prepareStatement(DELETE_STATEMENT_SQL)) {
            statement.setInt(1, code.getUser().getId());
            statement.setString(2, code.getCode());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenceException(e);
        }
    }
}
