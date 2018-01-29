package at.ac.tuwien.sepm.fridget.server.services;

import at.ac.tuwien.sepm.fridget.common.entities.BillShare;
import at.ac.tuwien.sepm.fridget.common.entities.PasswordResetCode;
import at.ac.tuwien.sepm.fridget.common.entities.User;
import at.ac.tuwien.sepm.fridget.common.entities.UserCredentials;
import at.ac.tuwien.sepm.fridget.common.exception.InvalidArgumentException;
import at.ac.tuwien.sepm.fridget.common.exception.PersistenceException;
import at.ac.tuwien.sepm.fridget.common.services.UserService;
import at.ac.tuwien.sepm.fridget.common.util.RandomString;
import at.ac.tuwien.sepm.fridget.common.util.UserValidator;
import at.ac.tuwien.sepm.fridget.server.persistence.PasswordResetCodeDAO;
import at.ac.tuwien.sepm.fridget.server.persistence.UserDAO;
import at.ac.tuwien.sepm.fridget.server.util.EmailContent;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service("userService")
public class ServerUserService implements UserService {

    private static final RandomString codeGenerator = new RandomString(24, new SecureRandom(), RandomString.ALPHANUMERIC);

    private final UserDAO userDAO;
    private final PasswordResetCodeDAO passwordResetCodeDAO;
    private final EmailService emailService;

    public ServerUserService(UserDAO userDAO, PasswordResetCodeDAO passwordResetCodeDAO, EmailService emailService) {
        this.userDAO = userDAO;
        this.passwordResetCodeDAO = passwordResetCodeDAO;
        this.emailService = emailService;
    }


    @Override
    public User findUser(UserCredentials credentials) throws PersistenceException, InvalidArgumentException {
        return userDAO.findUserByCredentials(credentials);
    }

    @Override
    public User createUser(User user) throws PersistenceException, InvalidArgumentException {
        UserValidator.getInstance().validateUserForRegistrationServer(user);
        if (userDAO.findUserByEmail(user.getEmail()) != null) {
            throw new InvalidArgumentException("Email already taken");
        }
        return userDAO.createUser(user);
    }

    @Override
    public User editUser(User user) throws PersistenceException, InvalidArgumentException {
        return userDAO.editUser(user);
    }

    @Override
    public void requestPasswordResetCode(String email) throws PersistenceException, InvalidArgumentException {
        User user = userDAO.findUserByEmail(email);
        if (user == null) {
            throw new InvalidArgumentException("The given email is invalid");
        }
        String code = codeGenerator.nextString();
        passwordResetCodeDAO.create(new PasswordResetCode(user, code));
        emailService.send(EmailContent.generateResetPasswordEmail(user, code));
    }

    @Override
    public boolean verifyPasswordResetCode(String email, String code) throws PersistenceException, InvalidArgumentException {
        User user = userDAO.findUserByEmail(email);
        if (user == null) throw new InvalidArgumentException("The given email is invalid");
        return passwordResetCodeDAO.verify(new PasswordResetCode(user, code));
    }

    @Override
    public User resetPassword(String email, String code, String password) throws PersistenceException, InvalidArgumentException {
        if (verifyPasswordResetCode(email, code)) {
            User user = userDAO.findUserByEmail(email);
            passwordResetCodeDAO.delete(new PasswordResetCode(user, code));
            user.setPassword(password);
            userDAO.editPassword(user);
            user.setPassword(null);
            return user;
        } else {
            throw new InvalidArgumentException("Invalid code");
        }
    }

    @Override
    public boolean compensateDebt(BillShare billShare, String paymentMethod) {
        return false;
    }

    @Override
    public User findUserByEmail(String email) throws PersistenceException, InvalidArgumentException {
        if (email == null) throw new InvalidArgumentException("Email must not be null!");
        return userDAO.findUserByEmail(email);
    }

    @Override
    public User findUserById(int userId) throws PersistenceException, InvalidArgumentException {
        if (userId < 1) throw new InvalidArgumentException("[User service] Invalid user id");
        return userDAO.findUserById(userId);
    }
}
