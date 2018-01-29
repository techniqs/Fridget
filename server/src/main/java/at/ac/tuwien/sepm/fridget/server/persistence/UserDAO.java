package at.ac.tuwien.sepm.fridget.server.persistence;

import at.ac.tuwien.sepm.fridget.common.entities.User;
import at.ac.tuwien.sepm.fridget.common.entities.UserCredentials;
import at.ac.tuwien.sepm.fridget.common.exception.InvalidArgumentException;
import at.ac.tuwien.sepm.fridget.common.exception.PersistenceException;

public interface UserDAO {

    User createUser(User user) throws PersistenceException, InvalidArgumentException;

    User editUser(User user) throws PersistenceException, InvalidArgumentException;

    User findUserByCredentials(UserCredentials credentials) throws PersistenceException, InvalidArgumentException;

    User findUserById(int userId) throws PersistenceException;

    User findUserByEmail(String userEmail) throws PersistenceException;

    void editPassword(User user) throws PersistenceException;

}
