package at.ac.tuwien.sepm.fridget.client.services;

import at.ac.tuwien.sepm.fridget.client.util.PropertiesKey;
import at.ac.tuwien.sepm.fridget.common.entities.User;
import at.ac.tuwien.sepm.fridget.common.entities.UserCredentials;
import at.ac.tuwien.sepm.fridget.common.exception.InvalidArgumentException;
import at.ac.tuwien.sepm.fridget.common.exception.PersistenceException;
import at.ac.tuwien.sepm.fridget.common.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;

@Service("authenticationService")
public class AuthenticationService implements IAuthenticationService {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    IPropertiesService propertiesService;

    @Autowired
    UserService userService;

    private User cachedUser = null;

    public ClientHttpRequestInterceptor getInterceptor() {
        return (request, body, execution) -> {
            UserCredentials storedCredentials = getStoredCredentials();
            if (storedCredentials != null) {
                request.getHeaders().add("Authorization", "Basic " + storedCredentials.getBasicAuthorizationHeader());
            }
            return execution.execute(request, body);
        };
    }

    @Override
    public void setCachedUser(User user) {
        this.cachedUser = user;
    }

    @Override
    public User getUser() {
        if (getStoredCredentials() == null) {
            return null;
        } else {
            if (cachedUser == null) {
                try {
                    cachedUser = userService.findUser(getStoredCredentials());
                } catch (PersistenceException | InvalidArgumentException e) {
                    LOG.error("Error while trying to fetch current user", e);
                }
            }
            return cachedUser;
        }
    }

    @Override
    public UserCredentials getStoredCredentials() {
        return UserCredentials.fromString(propertiesService.getProperty(PropertiesKey.CREDENTIALS));
    }

    @Override
    public void setStoredCredentials(UserCredentials credentials) {
        if (credentials == null) {
            propertiesService.removeProperty(PropertiesKey.CREDENTIALS);
        } else {
            propertiesService.setProperty(PropertiesKey.CREDENTIALS, credentials.toString());
        }
    }

    @Override
    public void logout() {
        this.cachedUser = null;
        propertiesService.removeProperty(PropertiesKey.CREDENTIALS);
    }

}
