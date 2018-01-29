package at.ac.tuwien.sepm.fridget.client.services;

import at.ac.tuwien.sepm.fridget.client.util.RestClient;
import at.ac.tuwien.sepm.fridget.common.entities.BillShare;
import at.ac.tuwien.sepm.fridget.common.entities.User;
import at.ac.tuwien.sepm.fridget.common.entities.UserCredentials;
import at.ac.tuwien.sepm.fridget.common.exception.InvalidArgumentException;
import at.ac.tuwien.sepm.fridget.common.exception.PersistenceException;
import at.ac.tuwien.sepm.fridget.common.services.UserService;
import at.ac.tuwien.sepm.fridget.common.util.ResetPasswordArguments;
import at.ac.tuwien.sepm.fridget.common.util.VerifyResetCodeArguments;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

@Service("userService")
public class ClientUserService implements UserService {

    @Autowired
    RestClient restClient;

    @Autowired
    ModelMapper modelMapper;

    @Override
    public User findUser(UserCredentials credentials) throws PersistenceException, InvalidArgumentException {
        try {
            return restClient.postForObject(
                "/user/login",
                modelMapper.map(credentials, UserCredentials.class),
                User.class
            );
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw new InvalidArgumentException(e.getResponseBodyAsString(), e);
            } else {
                throw new PersistenceException(e.getResponseBodyAsString(), e);
            }
        }
    }

    @Override
    public User createUser(User user) throws PersistenceException, InvalidArgumentException {
        try {
            return restClient.postForObject(
                "/user/create",
                modelMapper.map(user, User.class),
                User.class
            );
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw new InvalidArgumentException(e.getResponseBodyAsString(), e);
            } else {
                throw new PersistenceException(e.getResponseBodyAsString(), e);
            }
        }
    }

    @Override
    public User editUser(User user) throws PersistenceException, InvalidArgumentException {
        try {
            return restClient.postForObject(
                "/user/edit",
                modelMapper.map(user, User.class),
                User.class
            );
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw new InvalidArgumentException(e.getResponseBodyAsString(), e);
            } else {
                throw new PersistenceException(e.getResponseBodyAsString(), e);
            }
        }
    }

    @Override
    public void requestPasswordResetCode(String email) throws PersistenceException, InvalidArgumentException {
        try {
            restClient.postForObject(
                "/user/forgot-password",
                modelMapper.map(email, String.class),
                String.class
            );
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw new InvalidArgumentException(e.getResponseBodyAsString(), e);
            } else {
                throw new PersistenceException(e.getResponseBodyAsString(), e);
            }
        }
    }

    @Override
    public boolean verifyPasswordResetCode(String email, String code) throws PersistenceException, InvalidArgumentException {
        try {
            return restClient.postForObject(
                "/user/verify-reset-code",
                modelMapper.map(new VerifyResetCodeArguments(email, code), VerifyResetCodeArguments.class),
                Boolean.class
            );
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw new InvalidArgumentException(e.getResponseBodyAsString(), e);
            } else {
                throw new PersistenceException(e.getResponseBodyAsString(), e);
            }
        }
    }

    @Override
    public User resetPassword(String email, String code, String password) throws PersistenceException, InvalidArgumentException {
        try {
            return restClient.postForObject(
                "/user/reset-password",
                modelMapper.map(new ResetPasswordArguments(email, code, password), ResetPasswordArguments.class),
                User.class
            );
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw new InvalidArgumentException(e.getResponseBodyAsString(), e);
            } else {
                throw new PersistenceException(e.getResponseBodyAsString(), e);
            }
        }
    }

    @Override
    public boolean compensateDebt(BillShare billShare, String paymentMethod) {
        return false;
    }

    @Override
    public User findUserByEmail(String email) throws PersistenceException, InvalidArgumentException {
        try {
            return restClient.postForObject(
                "/user/findUserByEmail",
                email,
                User.class
            );
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw new InvalidArgumentException(e.getResponseBodyAsString(), e);
            } else {
                throw new PersistenceException(e.getResponseBodyAsString(), e);
            }
        }
    }

    @Override
    public User findUserById(int userId) throws PersistenceException, InvalidArgumentException {
        try {
            return restClient.postForObject(
                "/user/findUserById",
                userId,
                User.class
            );
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw new InvalidArgumentException(e.getResponseBodyAsString(), e);
            } else {
                throw new PersistenceException(e.getResponseBodyAsString(), e);
            }
        }
    }
}
