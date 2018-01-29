package at.ac.tuwien.sepm.fridget.client.services;

import at.ac.tuwien.sepm.fridget.common.entities.User;
import at.ac.tuwien.sepm.fridget.common.entities.UserCredentials;
import org.springframework.http.client.ClientHttpRequestInterceptor;

public interface IAuthenticationService {

    ClientHttpRequestInterceptor getInterceptor();

    void setCachedUser(User user);

    User getUser();

    UserCredentials getStoredCredentials();

    void setStoredCredentials(UserCredentials credentials);

    void logout();

}
