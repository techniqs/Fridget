package at.ac.tuwien.sepm.fridget.server.controllers;

import at.ac.tuwien.sepm.fridget.common.entities.Bill;
import at.ac.tuwien.sepm.fridget.common.entities.BillQuery;
import at.ac.tuwien.sepm.fridget.common.entities.User;
import at.ac.tuwien.sepm.fridget.common.exception.InvalidArgumentException;
import at.ac.tuwien.sepm.fridget.common.exception.PersistenceException;
import at.ac.tuwien.sepm.fridget.common.services.BillService;
import at.ac.tuwien.sepm.fridget.server.util.AuthUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.lang.invoke.MethodHandles;
import java.util.List;

@RestController
@RequestMapping("/bill")
public class BillController {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    private BillService billService;

    @RequestMapping(value = "/createBill", method = RequestMethod.POST)
    public Bill getBill(@RequestBody Bill bill, Authentication authentication) throws PersistenceException, InvalidArgumentException {
        LOG.info("called getBill");
        User user = AuthUtils.extractUser(authentication);

        return billService.createBill(bill, user);
    }

    @RequestMapping(value = "/editBill", method = RequestMethod.POST)
    public Bill editBill(@RequestBody Bill received) throws InvalidArgumentException, PersistenceException {
        LOG.info("called editBill");
        return billService.editBill(received);
    }

    @RequestMapping(value = "/deleteBill/{billID}")
    public boolean deleteBill(@PathVariable("billID") int billId) throws PersistenceException, InvalidArgumentException {
        LOG.info("called deleteBill");
        return billService.deleteBill(billId);
    }

    @RequestMapping(value = "/undoDeleteBill/{billID}")
    public boolean undoDeleteBill(@PathVariable("billID") int billId) throws PersistenceException, InvalidArgumentException {
        LOG.info("called undoDeleteBill");
        return billService.undoDeleteBill(billId);
    }

    @RequestMapping(value = "/listBills", method = RequestMethod.POST)
    public List<Bill> listAllBills(@RequestBody BillQuery billQuery) throws InvalidArgumentException, PersistenceException {
        LOG.info("called listAllBills");
        return billService.listAllBills(billQuery);
    }

    @RequestMapping(value = "/findBillById")
    public Bill findBillById(@RequestBody int billId) throws PersistenceException, InvalidArgumentException {
        LOG.info("called findBillById");
        return billService.findBillById(billId);
    }
}
