package at.ac.tuwien.sepm.fridget.server.persistence;

import at.ac.tuwien.sepm.fridget.common.entities.PasswordResetCode;
import at.ac.tuwien.sepm.fridget.common.exception.InvalidArgumentException;
import at.ac.tuwien.sepm.fridget.common.exception.PersistenceException;

public interface PasswordResetCodeDAO {

    void create(PasswordResetCode code) throws PersistenceException, InvalidArgumentException;

    boolean verify(PasswordResetCode code) throws PersistenceException, InvalidArgumentException;

    void delete(PasswordResetCode code) throws PersistenceException, InvalidArgumentException;

}
