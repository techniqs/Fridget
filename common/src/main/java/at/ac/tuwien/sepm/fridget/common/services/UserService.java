package at.ac.tuwien.sepm.fridget.common.services;

import at.ac.tuwien.sepm.fridget.common.entities.BillShare;
import at.ac.tuwien.sepm.fridget.common.entities.User;
import at.ac.tuwien.sepm.fridget.common.entities.UserCredentials;
import at.ac.tuwien.sepm.fridget.common.exception.InvalidArgumentException;
import at.ac.tuwien.sepm.fridget.common.exception.PersistenceException;

public interface UserService {

    User findUser(UserCredentials credentials) throws PersistenceException, InvalidArgumentException;

    /**
     * Create User
     *
     * @param user created User
     * @return User with UserID
     */
    User createUser(User user) throws PersistenceException, InvalidArgumentException;

    /**
     * Edit User
     * if a new password has been entered, trigger hashpassword
     *
     * @param user edited User
     * @return edited User with UserID
     */
    User editUser(User user) throws PersistenceException, InvalidArgumentException;

    void requestPasswordResetCode(String email) throws PersistenceException, InvalidArgumentException;

    boolean verifyPasswordResetCode(String email, String code) throws PersistenceException, InvalidArgumentException;

    User resetPassword(String email, String code, String password) throws PersistenceException, InvalidArgumentException;

    boolean compensateDebt(BillShare billShare, String paymentMethod) throws PersistenceException, InvalidArgumentException;

    User findUserByEmail(String email) throws PersistenceException, InvalidArgumentException;

    User findUserById(int userId) throws PersistenceException, InvalidArgumentException;

}
