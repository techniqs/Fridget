package at.ac.tuwien.sepm.fridget.server.controllers;

import at.ac.tuwien.sepm.fridget.common.entities.User;
import at.ac.tuwien.sepm.fridget.common.entities.UserCredentials;
import at.ac.tuwien.sepm.fridget.common.exception.InvalidArgumentException;
import at.ac.tuwien.sepm.fridget.common.exception.PersistenceException;
import at.ac.tuwien.sepm.fridget.common.services.UserService;
import at.ac.tuwien.sepm.fridget.common.util.ResetPasswordArguments;
import at.ac.tuwien.sepm.fridget.common.util.VerifyResetCodeArguments;
import at.ac.tuwien.sepm.fridget.server.util.AuthUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;

@RestController
@RequestMapping("/user")
public class UserController {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    User login(@RequestBody UserCredentials credentials) throws PersistenceException, InvalidArgumentException {
        LOG.info("called login");
        return userService.findUser(credentials);
    }

    @PostMapping("/create")
    User create(@RequestBody User user) throws PersistenceException, InvalidArgumentException {
        LOG.info("called create");
        return userService.createUser(user);
    }

    @PostMapping("/forgot-password")
    void forgotPassword(@RequestBody String email) throws PersistenceException, InvalidArgumentException {
        LOG.info("called forgot-password");
        userService.requestPasswordResetCode(email);
    }

    @PostMapping("/verify-reset-code")
    boolean verifyResetCode(@RequestBody VerifyResetCodeArguments args) throws PersistenceException, InvalidArgumentException {
        LOG.info("called verify-reset-code");
        return userService.verifyPasswordResetCode(args.getEmail(), args.getCode());
    }

    @PostMapping("/reset-password")
    void resetPassword(@RequestBody ResetPasswordArguments args) throws PersistenceException, InvalidArgumentException {
        LOG.info("called reset-password");
        userService.resetPassword(args.getEmail(), args.getCode(), args.getPassword());
    }

    @RequestMapping("/findUserByEmail")
    User findUserByEmail(@RequestBody String email) throws PersistenceException, InvalidArgumentException {
        LOG.info("called findUserByEmail");
        return userService.findUserByEmail(email);
    }

    @PostMapping("/edit")
    User editUser(@RequestBody User data, Authentication authentication) throws PersistenceException, InvalidArgumentException {
        LOG.info("called edit");
        User user = AuthUtils.extractUser(authentication);
        if (data.getId() != user.getId()) throw new InvalidArgumentException("Attempted edit of a different user");
        user.setName(data.getName());
        return userService.editUser(user);
    }

}
