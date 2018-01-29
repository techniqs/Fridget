package at.ac.tuwien.sepm.fridget.server.persistence;

import at.ac.tuwien.sepm.fridget.common.entities.User;
import at.ac.tuwien.sepm.fridget.common.entities.UserCredentials;
import at.ac.tuwien.sepm.fridget.common.exception.InvalidArgumentException;
import at.ac.tuwien.sepm.fridget.common.exception.PersistenceException;
import at.ac.tuwien.sepm.fridget.common.util.PasswordHelpers;
import at.ac.tuwien.sepm.fridget.server.TestBase;
import at.ac.tuwien.sepm.fridget.server.util.TestUtil;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Connection;
import java.sql.PreparedStatement;

import static org.assertj.core.api.Assertions.assertThat;

public class UserDAOH2Tests extends TestBase {

    private static final String INVALID_EMAIL = "invalid@example.org";
    private static final String INVALID_PASSWORD = "invalid";
    private static final String VALID_EMAIL = "qsefridget+userdaoh2tests@gmail.com";
    private static final String VALID_NAME = "UserDAOH2Tests";
    private static final String VALID_PASSWORD = "fridgettest";

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private TestUtil testUtil;

    @Autowired
    private Connection connection;

    @Override
    public void setUp() throws Exception {
        try (PreparedStatement stmt = connection.prepareStatement("INSERT INTO User (name, email, password) VALUES (?, ?, ?)")) {
            stmt.setString(1, VALID_NAME);
            stmt.setString(2, VALID_EMAIL);
            stmt.setString(3, PasswordHelpers.hashForStorage(PasswordHelpers.hashOnClient(VALID_PASSWORD)));
            stmt.execute();
        }
    }

    @Override
    public void tearDown() {
    }

    @Test
    public void findUserByCredentials_ShouldReturnNullOnInvalidEmail() throws PersistenceException, InvalidArgumentException {
        UserCredentials credentials = new UserCredentials(INVALID_EMAIL, PasswordHelpers.hashOnClient(INVALID_PASSWORD));

        assertThat(userDAO.findUserByCredentials(credentials)).isNull();
    }

    @Test
    public void findUserByCredentials_ShouldReturnNullOnInvalidPassword() throws PersistenceException, InvalidArgumentException {
        UserCredentials credentials = new UserCredentials(VALID_EMAIL, PasswordHelpers.hashOnClient(INVALID_PASSWORD));

        assertThat(userDAO.findUserByCredentials(credentials)).isNull();
    }

    @Test
    public void findUserByCredentials_ShouldReturnUserOnValidCredentials() throws PersistenceException, InvalidArgumentException {
        UserCredentials credentials = new UserCredentials(VALID_EMAIL, PasswordHelpers.hashOnClient(VALID_PASSWORD));

        User user = userDAO.findUserByCredentials(credentials);
        assertThat(user).isNotNull();
        assertThat(user.getId()).isPositive();
        assertThat(user.getName()).isEqualTo(VALID_NAME);
        assertThat(user.getEmail()).isEqualTo(VALID_EMAIL);
        assertThat(user.getPassword()).isNull();
    }

    @Test(expected = InvalidArgumentException.class)
    public void findUserByCredentials_ShouldThrowOnNullEmail() throws PersistenceException, InvalidArgumentException {
        UserCredentials credentials = new UserCredentials(null, PasswordHelpers.hashOnClient(VALID_PASSWORD));
        userDAO.findUserByCredentials(credentials);
    }

    @Test(expected = InvalidArgumentException.class)
    public void findUserByCredentials_ShouldThrowOnEmptyEmail() throws PersistenceException, InvalidArgumentException {
        UserCredentials credentials = new UserCredentials("", PasswordHelpers.hashOnClient(VALID_PASSWORD));
        userDAO.findUserByCredentials(credentials);
    }

    @Test(expected = InvalidArgumentException.class)
    public void findUserByCredentials_ShouldThrowOnNullPassword() throws PersistenceException, InvalidArgumentException {
        UserCredentials credentials = new UserCredentials(VALID_EMAIL, null);
        userDAO.findUserByCredentials(credentials);
    }

    @Test(expected = InvalidArgumentException.class)
    public void findUserByCredentials_ShouldThrowOnEmptyPassword() throws PersistenceException, InvalidArgumentException {
        UserCredentials credentials = new UserCredentials(VALID_EMAIL, "");
        userDAO.findUserByCredentials(credentials);
    }

    @Test
    public void createUser_ShouldReturnUserWithID() throws PersistenceException, InvalidArgumentException {
        User user = new User(-1, VALID_NAME, testUtil.getRandomEmail(), PasswordHelpers.hashOnClient(VALID_PASSWORD));
        User returnedUser = userDAO.createUser(user);
        assertThat(returnedUser).isNotNull();
        assertThat(returnedUser.getId()).isPositive();
    }

}
