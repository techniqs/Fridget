package at.ac.tuwien.sepm.fridget.client.util;

import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

import java.io.IOException;
import java.net.URI;

public class NamespacedClientHttpRequestFactory implements ClientHttpRequestFactory {

    private final String namespace;
    private final ClientHttpRequestFactory baseFactory;

    public NamespacedClientHttpRequestFactory(String namespace) {
        this(namespace, new SimpleClientHttpRequestFactory());
    }

    public NamespacedClientHttpRequestFactory(String namespace, ClientHttpRequestFactory baseFactory) {
        this.namespace = namespace;
        this.baseFactory = baseFactory;
    }

    @Override
    public ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod) throws IOException {
        URI result = uri.toString().startsWith("/")
            ? URI.create(namespace + uri.toString())
            : URI.create(namespace + "/" + uri.toString());
        return baseFactory.createRequest(result, httpMethod);
    }

}
