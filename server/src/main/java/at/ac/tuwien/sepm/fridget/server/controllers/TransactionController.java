package at.ac.tuwien.sepm.fridget.server.controllers;


import at.ac.tuwien.sepm.fridget.common.entities.BalanceToMembers;
import at.ac.tuwien.sepm.fridget.common.exception.InvalidArgumentException;
import at.ac.tuwien.sepm.fridget.common.exception.PersistenceException;
import at.ac.tuwien.sepm.fridget.common.services.TransactionService;
import at.ac.tuwien.sepm.fridget.server.util.AuthUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;

@RestController
@RequestMapping("/transaction")
public class TransactionController {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    private TransactionService transactionService;

    @RequestMapping(value = "/getBalance", method = RequestMethod.POST)
    public BigDecimal getBalance(@RequestBody int groupId, Authentication authentication) throws PersistenceException, InvalidArgumentException {
        LOG.info("called getBalance");
        return transactionService.getBalance(groupId, AuthUtils.extractUser(authentication).getId());
    }

    @RequestMapping(value = "/getBalanceToMembers", method = RequestMethod.POST)
    public BalanceToMembers getBalanceToMembers(@RequestBody int groupId, Authentication authentication) throws PersistenceException, InvalidArgumentException {
        LOG.info("called getBalanceToMembers");
        return new BalanceToMembers(transactionService.getBalanceToMembers(groupId, AuthUtils.extractUser(authentication).getId()));
    }

}
