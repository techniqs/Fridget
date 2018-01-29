package at.ac.tuwien.sepm.fridget.common.util;

import at.ac.tuwien.sepm.fridget.common.entities.User;
import at.ac.tuwien.sepm.fridget.common.exception.InvalidArgumentException;
import org.apache.commons.validator.routines.EmailValidator;

public class UserValidator {

    private static final UserValidator instance = new UserValidator();
    private static final String EMPTY_HASH = PasswordHelpers.hashOnClient("");

    private UserValidator() {
    }

    public static UserValidator getInstance() {
        return instance;
    }

    public void validateUserForRegistrationClient(User user) throws InvalidArgumentException {
        if (user == null) throw new InvalidArgumentException("User must not be null");
        validateName(user.getName());
        validateEmail(user.getEmail());
        validatePassword(user.getPassword());
    }

    public void validateUserForRegistrationServer(User user) throws InvalidArgumentException {
        if (user == null) throw new InvalidArgumentException("User must not be null");
        validateName(user.getName());
        validateEmail(user.getEmail());
        validatePasswordHash(user.getPassword());
    }

    public void validateName(String name) throws InvalidArgumentException {
        if (name == null || name.isEmpty()) {
            throw new InvalidArgumentException("Name is required");
        }
    }

    public void validateEmail(String email) throws InvalidArgumentException {
        if (email == null || email.isEmpty()) {
            throw new InvalidArgumentException("Email is required");
        } else if (!EmailValidator.getInstance().isValid(email)) {
            throw new InvalidArgumentException("Email must be valid");
        }
    }

    public void validatePassword(String password) throws InvalidArgumentException {
        if (password == null || password.isEmpty()) {
            throw new InvalidArgumentException("Password is required");
        } else if (!PasswordHelpers.isValidPassword(password)) {
            throw new InvalidArgumentException("Password must have at least " + PasswordHelpers.MIN_LENGTH + " characters");
        }
    }

    public void validatePasswordForLogin(String password) throws InvalidArgumentException {
        if (password == null || password.isEmpty()) {
            throw new InvalidArgumentException("Password is required");
        }
    }

    public void validatePasswordHash(String password) throws InvalidArgumentException {
        if (password == null || password.isEmpty()) {
            throw new InvalidArgumentException("Password is required");
        } else if (!password.matches("[a-f0-9]{64}")) {
            throw new InvalidArgumentException("Password must be a lowercase hex SHA-256 string");
        } else if (password.equals(EMPTY_HASH)) {
            throw new InvalidArgumentException("Password must not be empty");
        }
    }

}
