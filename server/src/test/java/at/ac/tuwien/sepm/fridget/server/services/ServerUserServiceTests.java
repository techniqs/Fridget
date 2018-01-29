package at.ac.tuwien.sepm.fridget.server.services;

import at.ac.tuwien.sepm.fridget.common.entities.User;
import at.ac.tuwien.sepm.fridget.common.entities.UserCredentials;
import at.ac.tuwien.sepm.fridget.common.exception.InvalidArgumentException;
import at.ac.tuwien.sepm.fridget.common.exception.PersistenceException;
import at.ac.tuwien.sepm.fridget.common.util.PasswordHelpers;
import at.ac.tuwien.sepm.fridget.server.TestBase;
import at.ac.tuwien.sepm.fridget.server.persistence.UserDAO;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class ServerUserServiceTests extends TestBase {

    @Override
    public void setUp() throws Exception {
    }

    @Override
    public void tearDown() {
    }

    @Test
    public void findUser_returnUserFromDao() throws InvalidArgumentException, PersistenceException {
        UserDAO userDAO = mock(UserDAO.class);
        ServerUserService userService = new ServerUserService(userDAO, null, null);

        UserCredentials credentials = new UserCredentials("testemail@example.org", "testpassword");

        User testUser = new User();
        testUser.setId(1);
        testUser.setEmail("testemail@example.org");
        testUser.setName("Tester");

        when(userDAO.findUserByCredentials(any())).thenReturn(testUser);

        assertThat(userService.findUser(credentials)).isEqualTo(testUser);

        verify(userDAO).findUserByCredentials(credentials);
    }

    @Test
    public void findUser_returnNullFromDao() throws InvalidArgumentException, PersistenceException {
        UserDAO userDAO = mock(UserDAO.class);
        ServerUserService userService = new ServerUserService(userDAO, null, null);

        UserCredentials credentials = new UserCredentials("testemail@example.org", "testpassword");

        when(userDAO.findUserByCredentials(any())).thenReturn(null);

        assertThat(userService.findUser(credentials)).isNull();

        verify(userDAO).findUserByCredentials(credentials);
    }

    @Test(expected = InvalidArgumentException.class)
    public void findUser_passInvalidArgumentException() throws InvalidArgumentException, PersistenceException {
        UserDAO userDAO = mock(UserDAO.class);
        ServerUserService userService = new ServerUserService(userDAO, null, null);
        when(userDAO.findUserByCredentials(any())).thenThrow(new InvalidArgumentException("Test"));
        userService.findUser(new UserCredentials(null, null));
    }

    @Test(expected = PersistenceException.class)
    public void findUser_passPersistenceException() throws InvalidArgumentException, PersistenceException {
        UserDAO userDAO = mock(UserDAO.class);
        ServerUserService userService = new ServerUserService(userDAO, null, null);
        when(userDAO.findUserByCredentials(any())).thenThrow(new PersistenceException("Test"));
        userService.findUser(new UserCredentials(null, null));
    }


    @Test(expected = InvalidArgumentException.class)
    public void createUser_checksForEmailAvailability() throws InvalidArgumentException, PersistenceException {
        UserDAO userDAO = mock(UserDAO.class);
        ServerUserService userService = new ServerUserService(userDAO, null, null);
        User user = new User(-1, "Test", "test@test.org", PasswordHelpers.hashOnClient("testtest"));
        when(userDAO.findUserByEmail(any())).thenReturn(user);
        try {
            userService.createUser(user);
        } catch (InvalidArgumentException e) {
            verify(userDAO).findUserByEmail(eq("test@test.org"));
            throw e;
        }
    }

    @Test
    public void createUser_returnUserFromDAO() throws InvalidArgumentException, PersistenceException {
        UserDAO userDAO = mock(UserDAO.class);
        ServerUserService userService = new ServerUserService(userDAO, null, null);
        User user = new User(-1, "Test", "test@test.org", PasswordHelpers.hashOnClient("testtest"));
        User returnedUser = new User();

        when(userDAO.findUserByEmail(any())).thenReturn(null);
        when(userDAO.createUser(any())).thenReturn(returnedUser);

        assertThat(userService.createUser(user)).isEqualTo(returnedUser);
        verify(userDAO).findUserByEmail(eq("test@test.org"));
        verify(userDAO).createUser(user);
    }

    @Test(expected = InvalidArgumentException.class)
    public void createUser_failsOnEmptyUser() throws InvalidArgumentException, PersistenceException {
        ServerUserService userService = new ServerUserService(null, null, null);
        userService.createUser(null);
    }

    @Test(expected = InvalidArgumentException.class)
    public void createUser_passInvalidArgumentException() throws InvalidArgumentException, PersistenceException {
        UserDAO userDAO = mock(UserDAO.class);
        ServerUserService userService = new ServerUserService(userDAO, null, null);
        when(userDAO.findUserByEmail(any())).thenReturn(null);
        when(userDAO.createUser(any())).thenThrow(new InvalidArgumentException("Test"));
        userService.createUser(new User(-1, "Test", "test@test.org", PasswordHelpers.hashOnClient("testtest")));
    }

    @Test(expected = PersistenceException.class)
    public void createUser_passPersistenceException() throws InvalidArgumentException, PersistenceException {
        UserDAO userDAO = mock(UserDAO.class);
        ServerUserService userService = new ServerUserService(userDAO, null, null);
        when(userDAO.findUserByEmail(any())).thenReturn(null);
        when(userDAO.createUser(any())).thenThrow(new PersistenceException("Test"));
        userService.createUser(new User(-1, "Test", "test@test.org", PasswordHelpers.hashOnClient("testtest")));
    }

}
