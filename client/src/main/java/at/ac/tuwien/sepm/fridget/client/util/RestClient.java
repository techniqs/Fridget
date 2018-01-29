package at.ac.tuwien.sepm.fridget.client.util;

import at.ac.tuwien.sepm.fridget.client.services.IAuthenticationService;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class RestClient extends RestTemplate {

    private RestClient(IAuthenticationService authenticationService) {
        super(new NamespacedClientHttpRequestFactory("http://localhost:8080"));

        this.getInterceptors().add(authenticationService.getInterceptor());
    }

}
