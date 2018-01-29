package at.ac.tuwien.sepm.fridget.server.services;

import at.ac.tuwien.sepm.fridget.common.entities.Bill;
import at.ac.tuwien.sepm.fridget.common.entities.BillShare;
import at.ac.tuwien.sepm.fridget.common.entities.User;
import at.ac.tuwien.sepm.fridget.common.exception.InvalidArgumentException;
import at.ac.tuwien.sepm.fridget.common.exception.PersistenceException;
import at.ac.tuwien.sepm.fridget.common.services.BillService;
import at.ac.tuwien.sepm.fridget.common.services.BillShareService;
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

public class ServerBillShareServiceTests extends TestBase {

    @Autowired
    private TestUtil testUtil;
    @Autowired
    private BillShareService billShareService;

    @Autowired
    private BillService billService;

    private BillShare share;

    private Bill bill;

    @Before
    public void setUp() throws PersistenceException, InvalidArgumentException {
        share = testUtil.createValidBillShare();
        // Write a valid bill to db for the bill share to reference
        bill = testUtil.createValidBill();
        bill = billService.createBill(bill, bill.getUser());
        share.setBill(bill);

    }

    @After
    public void tearDown() {
        share = null;
        try {
            billService.deleteBill(bill.getId());
        } catch (InvalidArgumentException e) {
            e.printStackTrace();
        } catch (PersistenceException e) {
            e.printStackTrace();
        }
    }

    /* EVEN BILL SHARES */


    /*@Test
    public void getAllBillSharesForBillWithInvalidBillId_ShouldNotFail() throws PersistenceException {
        assertThat(billShareService.getAllBillSharesForBill(-1).isEmpty()).isTrue();
    }

    @Test
    public void getAllBillSharesForBillWithValidBillId_ShouldNotFail() throws PersistenceException {
        assertThat(billShareService.getAllBillSharesForBill(1).isEmpty()).isFalse();
    }*/


    @Test
    public void createValidEvenBillShare_ReturnBillShareWithID() throws PersistenceException, InvalidArgumentException {
        share.setAmount(BigDecimal.ZERO);// Even shares must have 0 as value
        share = billShareService.createBillShare(share, 1);

        assertThat(share.getAmount()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(share.getUser().getId()).isEqualTo(2);
        assertThat(share.getId()).isGreaterThan(0);
        assertThat(share.getBill()).isNotNull();
    }

    @Test(expected = InvalidArgumentException.class)
    public void createEvenBillShareWithAmountBiggerZero_throwInvalidArgumentException()
        throws PersistenceException, InvalidArgumentException {
        share.setAmount(BigDecimal.ONE);
        billShareService.createBillShare(share, 1);
    }

    /* GENERAL TESTS */

    @Test(expected = InvalidArgumentException.class)
    public void createBillShareWithoutUser_throwInvalidArgumentException()
        throws InvalidArgumentException, PersistenceException {
        share.setUser(null);
        billShareService.createBillShare(share, 1);
    }

    @Test(expected = InvalidArgumentException.class)
    public void createBillShareWithoutBill_throwInvalidArgumentException()
        throws InvalidArgumentException, PersistenceException {
        share.setBill(null);
        billShareService.createBillShare(share, 1);
    }

    @Test(expected = InvalidArgumentException.class)
    public void createBillShareWithoutAmount_throwInvalidArgumentException()
        throws InvalidArgumentException, PersistenceException {
        share.setAmount(null);
        billShareService.createBillShare(share, 1);
    }

    @Test(expected = InvalidArgumentException.class)
    public void createBillShareWithAmountSmallerZero_throwInvalidArgumentException()
        throws PersistenceException, InvalidArgumentException {
        share.setAmount(BigDecimal.valueOf(-1));
        billShareService.createBillShare(share, 1);
    }

    @Test(expected = InvalidArgumentException.class)
    public void editBillShareWithInvalidBill_ThrowInvalidArgumentException() throws InvalidArgumentException, PersistenceException {
        billShareService.editBillShare(null, -1);
    }

    @Test
    public void editBillShare_ReturnEditedBillShare() throws InvalidArgumentException, PersistenceException {
        BillShare billShare = billShareService.readBillShare(2);
        billShare.setAmount(BigDecimal.ZERO);
        billShare.setUser(new User(2, "Bob", "qsefridget+bob@gmail.com", "fridgetbob"));
        billShareService.editBillShare(billShare, 1);
        assertThat(billShareService.readBillShare(2).getUser().getId()).isEqualTo(2);
    }

    @Test (expected = InvalidArgumentException.class)
    public void deleteBillShare_ThrowInvalidArgumentException() throws InvalidArgumentException, PersistenceException {
        billShareService.deleteBillShare(-1);
    }

    @Test
    public void deleteBillShare_DeleteBillShareFromDatabase() throws InvalidArgumentException, PersistenceException {
        billShareService.deleteBillShare(8);
        assertThat(billShareService.readBillShare(8)).isNull();
    }

    @Test(expected = InvalidArgumentException.class)
    public void readBillShareWithInvalidId_ThrowInvalidArgumentException() throws InvalidArgumentException, PersistenceException {
        billShareService.readBillShare(-1);
    }
    @Test
    public void readBillShare_ReturnBillShare() throws InvalidArgumentException, PersistenceException {
        BillShare billShare = billShareService.readBillShare(2);
        assertThat(billShare.getId()).isEqualTo(2);
        assertThat(billShare.getAmount().intValue()).isEqualTo(0);
        assertThat(billShare.getUser().getId()).isEqualTo(5);
        assertThat(billShare.getBill().getId()).isEqualTo(1);
    }

    @Test (expected = InvalidArgumentException.class)
    public void getAllBillSharesForBillWithInvalidBill_ThrowInvalidArgumentException() throws InvalidArgumentException, PersistenceException {
        billShareService.getAllBillSharesForBill(testUtil.createInvalidBill());
    }

    @Test
    public void getAllBillSharesForBill_ReturnListOfBillShares() throws InvalidArgumentException, PersistenceException {
        Bill bill = testUtil.createValidBill();
        bill.setId(10);
        List<BillShare> billShares = billShareService.getAllBillSharesForBill(bill);
        Assertions.assertThat(billShares.size()).isEqualTo(2);
        assertThat(billShares.get(0).getId()).isGreaterThan(0);
        assertThat(billShares.get(1).getId()).isGreaterThan(0);
    }
}
