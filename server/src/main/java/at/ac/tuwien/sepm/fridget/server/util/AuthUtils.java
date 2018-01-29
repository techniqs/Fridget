package at.ac.tuwien.sepm.fridget.server.util;

import at.ac.tuwien.sepm.fridget.common.entities.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

public class AuthUtils {

    public static boolean isAuthenticatedUser(Authentication authentication) {
        return authentication != null && authentication instanceof UsernamePasswordAuthenticationToken;
    }

    public static User extractUser(Authentication authentication) {
        return (User) authentication.getPrincipal();
    }

}
