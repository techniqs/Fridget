package at.ac.tuwien.sepm.fridget.server.util;

import com.google.api.gax.core.CredentialsProvider;
import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;

import java.io.IOException;

public class ResourceGoogleCredentialsProvider implements CredentialsProvider {

    private final String resourceName;

    public ResourceGoogleCredentialsProvider(String resourceName) {
        this.resourceName = resourceName;
    }

    @Override
    public Credentials getCredentials() throws IOException {
        return GoogleCredentials.fromStream(this.getClass().getClassLoader().getResourceAsStream(resourceName));
    }

}
