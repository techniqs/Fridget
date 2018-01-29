package at.ac.tuwien.sepm.fridget.server.controllers;

import at.ac.tuwien.sepm.fridget.common.entities.User;
import at.ac.tuwien.sepm.fridget.server.util.AuthUtils;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class IndexController {

    @GetMapping
    String index(Authentication authentication) {
        String name = "Guest";
        if (AuthUtils.isAuthenticatedUser(authentication)) {
            name = ((User) authentication.getPrincipal()).getName();
        }
        return "Hello " + name + "!<br>" +
            "<a href=\"/greeting/World\">Test Greeting</a>";
    }

}
