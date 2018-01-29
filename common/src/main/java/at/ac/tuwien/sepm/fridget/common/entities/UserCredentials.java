package at.ac.tuwien.sepm.fridget.common.entities;

import org.springframework.util.Base64Utils;

import java.nio.charset.Charset;

public class UserCredentials {

    private String email;
    private String password;


    public UserCredentials() {
        this(null, null);
    }

    public UserCredentials(String email, String password) {
        this.email = email;
        this.password = password;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBasicAuthorizationHeader() {
        String content = getEmail() + ":" + getPassword();
        return Base64Utils.encodeToString(content.getBytes(Charset.forName("UTF-8")));
    }

    public static UserCredentials fromString(String value) {
        if (value == null) return null;
        String decoded = new String(Base64Utils.decodeFromString(value), Charset.forName("UTF-8"));
        if (!decoded.contains(":")) return null;

        String email = decoded.substring(0, decoded.indexOf(":"));
        String password = decoded.substring(email.length() + 1);

        return new UserCredentials(email, password);
    }

    @Override
    public String toString() {
        return getBasicAuthorizationHeader();
    }

}
