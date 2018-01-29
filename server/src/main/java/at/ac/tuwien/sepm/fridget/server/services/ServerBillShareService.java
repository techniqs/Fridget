package at.ac.tuwien.sepm.fridget.server.services;

import at.ac.tuwien.sepm.fridget.common.entities.Bill;
import at.ac.tuwien.sepm.fridget.common.entities.BillShare;
import at.ac.tuwien.sepm.fridget.common.exception.InvalidArgumentException;
import at.ac.tuwien.sepm.fridget.common.exception.PersistenceException;
import at.ac.tuwien.sepm.fridget.common.services.BillService;
import at.ac.tuwien.sepm.fridget.common.services.BillShareService;
import at.ac.tuwien.sepm.fridget.common.services.GroupService;
import at.ac.tuwien.sepm.fridget.common.util.ShareTechniqueId;
import at.ac.tuwien.sepm.fridget.server.persistence.BillDAO;
import at.ac.tuwien.sepm.fridget.server.persistence.BillShareDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service("billShareService")
public class ServerBillShareService implements BillShareService {

    @Autowired
    BillShareDAO billShareDAO;
    @Autowired
    BillService billService;
    @Autowired
    BillDAO billDAO;
    @Autowired
    GroupService groupService;

    @Override
    public BillShare createBillShare(BillShare billShare, int billId) throws PersistenceException, InvalidArgumentException {
        validateBillShare(billShare);
        return billShareDAO.createBillShare(billShare, billId);
    }

    @Override
    public BillShare editBillShare(BillShare billShare, int billId) throws PersistenceException, InvalidArgumentException {
        validateBillShare(billShare);
        return billShareDAO.editBillShare(billShare, billId);
    }

    @Override
    public void deleteBillShare(int id) throws PersistenceException, InvalidArgumentException {
        if(id < 0)
            throw new InvalidArgumentException("ID must not be negativ");
        billShareDAO.deleteBillShare(id);
    }

    @Override
    public void deleteBillSharesForBillId(int billId) throws PersistenceException, InvalidArgumentException {
        if (billId < 0)
            throw new InvalidArgumentException("ID must not be negativ");
        billShareDAO.deleteBillSharesForBillId(billId);
    }

    @Override
    public BillShare readBillShare(int id) throws PersistenceException, InvalidArgumentException {
        if(id < 0)
            throw new InvalidArgumentException("ID must not be negativ");
        return billShareDAO.findBillShareById(id);
    }

    @Override
    public List<BillShare> getAllBillSharesForBill(Bill bill) throws PersistenceException, InvalidArgumentException {
        if (billService.validateBill(bill))
            return billShareDAO.getAllBillSharesForBill(bill);
        return null;
    }

    /**
     * Bill share validation
     *
     * @param billShare the bill share to validate
     */
    private void validateBillShare(BillShare billShare) throws InvalidArgumentException {
        if (billShare == null)
            throw new InvalidArgumentException("Bill share must not be null!");
        StringBuilder message = new StringBuilder();
        String newLine = System.lineSeparator();
        if (billShare.getBill() == null || billShare.getBill().getId() < 0) {
            message.append("Bill of bill share must be valid!");
        }
        if (billShare.getAmount() == null) {
            message.append("Bill share amount must not be null.").append(newLine);
        }
        // PERCENTAGE and MANUAL share techniques must have values > 0
        else if (billShare.getBill() != null
            && !billShare.getBill().getShareTechniqueId().equals(ShareTechniqueId.EVEN)
            && billShare.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            message.append("Bill share amount must not be <= 0.");
        }
        // PERCENTAGE bill shares must have values not bigger than 100
        else if (billShare.getBill() != null
            && billShare.getBill().getShareTechniqueId().equals(ShareTechniqueId.PERCENTAGE)
            && billShare.getAmount().compareTo(BigDecimal.valueOf(100)) > 0) {
            message.append("Percentage bill share amount must not be greater than 100!");
        }
        // EVEN bill shares must have 0 as value
        else if (billShare.getBill() != null
            && billShare.getBill().getShareTechniqueId().equals(ShareTechniqueId.EVEN)
            && billShare.getAmount().compareTo(BigDecimal.ZERO) != 0) {
            message.append("For the EVEN share technique amount must be zero!");
        }
        if (billShare.getUser() == null) {
            message.append("User of bill share must not be null!");
        }
        if (!message.toString().equals(""))
            throw new InvalidArgumentException(message.toString());
    }
}
