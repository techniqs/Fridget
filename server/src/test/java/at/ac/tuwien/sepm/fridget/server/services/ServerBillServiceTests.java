package at.ac.tuwien.sepm.fridget.server.services;

import at.ac.tuwien.sepm.fridget.common.entities.Bill;
import at.ac.tuwien.sepm.fridget.common.entities.BillQuery;
import at.ac.tuwien.sepm.fridget.common.entities.Group;
import at.ac.tuwien.sepm.fridget.common.exception.InvalidArgumentException;
import at.ac.tuwien.sepm.fridget.common.exception.PersistenceException;
import at.ac.tuwien.sepm.fridget.common.services.BillService;
import at.ac.tuwien.sepm.fridget.server.TestBase;
import at.ac.tuwien.sepm.fridget.server.util.TestUtil;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

public class ServerBillServiceTests extends TestBase {

    @Autowired
    private BillService billService;

    @Autowired
    private TestUtil testUtil;

    @Override
    public void setUp() throws Exception {

    }

    @Override
    public void tearDown() {

    }

    @Test
    public void createValidBill_returnValidBillWithId() throws InvalidArgumentException, PersistenceException {
        Bill bill = testUtil.createValidBill();
        bill = billService.createBill(bill, bill.getUser());

        assertThat(bill.getId()).isGreaterThan(-1);
    }

    @Test(expected = InvalidArgumentException.class)
    public void createBillWithoutTitle_throwInvalidArgumentException() throws InvalidArgumentException, PersistenceException {
        Bill bill = testUtil.createValidBill();
        bill.setTitle("");
        billService.createBill(bill, null);
    }

    @Test(expected = InvalidArgumentException.class)
    public void createBillWithoutAmount_throwInvalidArgumentException() throws InvalidArgumentException, PersistenceException {
        Bill bill = testUtil.createValidBill();
        bill.setAmount(null);
        billService.createBill(bill, null);
    }

    @Test(expected = InvalidArgumentException.class)
    public void createBillWithAmountSmallerZero_throwInvalidArgumentException() throws InvalidArgumentException, PersistenceException {
        Bill bill = testUtil.createValidBill();
        bill.setAmount(BigDecimal.valueOf(-1));
        billService.createBill(bill, null);
    }

    @Test(expected = InvalidArgumentException.class)
    public void createBillWithoutShareTechnique_throwInvalidArgumentException() throws InvalidArgumentException, PersistenceException {
        Bill bill = testUtil.createValidBill();
        bill.setShareTechniqueId(null);
        billService.createBill(bill, null);
    }

    @Test(expected = InvalidArgumentException.class)
    public void createBillWithoutCurrency_throwInvalidArgumentException() throws InvalidArgumentException, PersistenceException {
        Bill bill = testUtil.createValidBill();
        bill.setCurrency(null);
        billService.createBill(bill, null);
    }

    @Test
    public void deleteExistingBill_ShouldReturnTrue() throws PersistenceException, InvalidArgumentException {
        assertThat(billService.deleteBill(1)).isTrue();
    }

    @Test(expected = InvalidArgumentException.class)
    public void deleteBillNegativeID_ShouldThrowIllegalArgumentException() throws PersistenceException, InvalidArgumentException {
        billService.deleteBill(-1);
    }

    @Test
    public void editBillDescription_ShouldReturnEditedBill() throws InvalidArgumentException, PersistenceException {
        BillQuery billQuery = new BillQuery();
        Group group = new Group();
        group.setId(1);
        billQuery.setGroup(group);
        Bill bill = billService.listAllBills(billQuery).get(5);
        bill.setDescription("test edited description");
        bill = billService.editBill(bill);
        assertThat(bill.getDescription()).isEqualTo("test edited description");
    }

    @Test(expected = InvalidArgumentException.class)
    public void editInvalidBill_throwInvalidArgumentException() throws InvalidArgumentException, PersistenceException {
        billService.editBill(null);
    }

    @Test
    public void undoDeleteOfBill_ShouldReturnTrue() throws InvalidArgumentException, PersistenceException {
        Bill bill = testUtil.createValidBill();
        bill.setDeleted(true);
        billService.createBill(bill, bill.getUser());
        assertThat(billService.undoDeleteBill(bill.getId())).isTrue();

    }

    @Test(expected = InvalidArgumentException.class)
    public void undoDeleteNeativBillID_throwInvalidArgumentException() throws PersistenceException, InvalidArgumentException {
        billService.undoDeleteBill(-1);
    }

    @Test
    public void listAllBills_returnBillsOfGroupIDOnly() throws InvalidArgumentException, PersistenceException {
        BillQuery billQuery = new BillQuery();
        Group group = new Group();
        group.setId(1);
        billQuery.setGroup(group);
        List<Bill> bills = billService.listAllBills(billQuery);
        assertThat(!bills.isEmpty());
        bills.forEach(bill -> assertThat(bill.getGroup().getId() == 1));
    }

    @Test
    public void listAllBills_returnBillsInTimeRange() throws InvalidArgumentException, PersistenceException {
        BillQuery billQuery = new BillQuery();
        Group group = testUtil.createValidGroup();
        group.setId(1);
        billQuery.setGroup(group);
        billQuery.setMinDate(null);
        LocalDateTime now = LocalDateTime.now();
        billQuery.setMaxDate(now);
        List<Bill> bills = billService.listAllBills(billQuery);
        assertThat(!bills.isEmpty());
        bills.forEach(bill -> assertTrue(bill.getCreatedAt().isBefore(LocalDateTime.now().plusSeconds(2))));
    }

    @Test(expected = InvalidArgumentException.class)
    public void listAllBills_throwIllegalArgumentException() throws PersistenceException, InvalidArgumentException {
        billService.listAllBills(null);
    }
}
