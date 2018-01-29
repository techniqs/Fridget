package at.ac.tuwien.sepm.fridget.server.persistence;

import at.ac.tuwien.sepm.fridget.common.entities.Bill;
import at.ac.tuwien.sepm.fridget.common.entities.BillShare;
import at.ac.tuwien.sepm.fridget.common.exception.InvalidArgumentException;
import at.ac.tuwien.sepm.fridget.common.exception.PersistenceException;
import at.ac.tuwien.sepm.fridget.server.TestBase;
import at.ac.tuwien.sepm.fridget.server.util.TestUtil;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class BillShareDAOH2Tests extends TestBase {

    @Autowired
    BillShareDAO billShareDAO;

    @Autowired
    TestUtil testUtil;

    private BillShare share;

    @Before
    public void setUp() throws PersistenceException, InvalidArgumentException {
        share = testUtil.createValidBillShare();
        Bill validBill = testUtil.createValidBill();
        validBill.setId(1);
        share.setBill(validBill);

    }

    @After
    public void tearDown() {
        share = null;
    }

    @Test
    public void createValidBillShare_ShouldReturnBillShareWithID()
        throws PersistenceException, InvalidArgumentException {
        BigDecimal amount = share.getAmount();
        share = billShareDAO.createBillShare(share, 1);
        assertThat(share.getId()).isNotNull();
        assertThat(share.getId()).isGreaterThan(0);
        assertThat(share.getAmount()).isEqualByComparingTo(amount);
        assertThat(share.getBill().getId()).isEqualTo(1);
        assertThat(share.getUser().getId()).isEqualTo(2);
    }

    @Test(expected = PersistenceException.class)
    public void createBillShareWithoutBill_ThrowPersistenceException()
        throws PersistenceException, InvalidArgumentException {
        share.setBill(null);
        billShareDAO.createBillShare(share, -1);
    }

    @Test(expected = PersistenceException.class)
    public void createBillShareWithoutAmount_ThrowPersistenceException()
        throws PersistenceException, InvalidArgumentException {
        share.setAmount(null);
        billShareDAO.createBillShare(share, -1);
    }

    @Test(expected = PersistenceException.class)
    public void createBillShareWithoutUser_ThrowPersistenceException()
        throws InvalidArgumentException, PersistenceException {
        share.setUser(null);
        billShareDAO.createBillShare(share, -1);
    }

    @Test(expected = InvalidArgumentException.class)
    public void deleteBillShareWithInvalidBillShare_ThrowInvalidArgumentException() throws PersistenceException, InvalidArgumentException {
        billShareDAO.deleteBillShare(-1);
    }

    @Test
    public void deleteBillShare_ReturnNull() throws PersistenceException, InvalidArgumentException {
        billShareDAO.deleteBillShare(8);
        assertThat(billShareDAO.findBillShareById(8)).isNull();
    }

    @Test(expected = InvalidArgumentException.class)
    public void editBillShare_ThrowInvalidArgumentException() throws PersistenceException, InvalidArgumentException {
        billShareDAO.editBillShare(null, -1);
    }

    @Test
    public void editBillShare_ReturnEditedBillShare() throws PersistenceException, InvalidArgumentException {
        BillShare billShare = billShareDAO.findBillShareById(1);
        billShare.setAmount(BigDecimal.valueOf(666));
        billShareDAO.editBillShare(billShare, 1);
        assertThat(billShareDAO.findBillShareById(1).getAmount().intValue()).isEqualTo(666);
    }

    @Test
    public void findBillShareByIdWithUnknownId_ReturnNull() throws PersistenceException, InvalidArgumentException {
        billShareDAO.findBillShareById(999);
    }

    @Test
    public void findBillShareById_ReturnBillShare() throws PersistenceException, InvalidArgumentException {
        BillShare billShare = billShareDAO.findBillShareById(1);
        assertThat(billShare.getId()).isEqualTo(1);
        assertThat(billShare.getAmount().intValue()).isEqualTo(0);
        assertThat(billShare.getUser().getId()).isEqualTo(2);
        assertThat(billShare.getBill().getId()).isEqualTo(1);
    }

    @Test(expected = InvalidArgumentException.class)
    public void getBillSharesForBill_ThrowInvalidArgumentException() throws PersistenceException, InvalidArgumentException {
        billShareDAO.getAllBillSharesForBill(testUtil.createValidBill());
    }

    @Test
    public void getBillSharesForBill_ReturnListWithUsers() throws PersistenceException, InvalidArgumentException {
        Bill bill = testUtil.createValidBill();
        bill.setId(10);
        List<BillShare> billShares = billShareDAO.getAllBillSharesForBill(bill);
        Assertions.assertThat(billShares.size()).isEqualTo(2);
        assertThat(billShares.get(0).getId()).isGreaterThan(0);
        assertThat(billShares.get(1).getId()).isGreaterThan(0);
    }
}
