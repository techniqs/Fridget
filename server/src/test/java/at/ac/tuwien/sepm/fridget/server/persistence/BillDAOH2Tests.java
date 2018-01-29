package at.ac.tuwien.sepm.fridget.server.persistence;

import at.ac.tuwien.sepm.fridget.common.entities.*;
import at.ac.tuwien.sepm.fridget.common.exception.InvalidArgumentException;
import at.ac.tuwien.sepm.fridget.common.exception.PersistenceException;
import at.ac.tuwien.sepm.fridget.common.util.Currency;
import at.ac.tuwien.sepm.fridget.common.util.ShareTechniqueId;
import at.ac.tuwien.sepm.fridget.server.TestBase;
import at.ac.tuwien.sepm.fridget.server.util.TestUtil;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class BillDAOH2Tests extends TestBase {

    @Autowired
    BillDAO billDAO;

    @Autowired
    TestUtil testUtil;

    @Test
    public void createValidBill_ShouldReturnBillWithID() throws PersistenceException, InvalidArgumentException {
        Bill bill = testUtil.createValidBill();
        bill = billDAO.createBill(bill);

        assertThat(bill.getId()).isNotNull();
        assertThat(bill.getId()).isGreaterThan(-1);
        assertThat(bill.getAmount()).isEqualByComparingTo(BigDecimal.TEN);
        assertThat(bill.getShareTechniqueId()).isEqualTo(ShareTechniqueId.EVEN);
        assertThat(bill.getTitle()).isEqualTo("TestGroceries");
        assertThat(bill.getDescription()).isEqualTo("Christmas food");
        assertThat(bill.getCreatedAt()).isNotNull();
        assertThat(bill.getUpdatedAt()).isNotNull();
        assertThat(bill.isDeleted()).isEqualTo(false);
        assertThat(bill.getCurrency()).isEqualByComparingTo(Currency.EUR);
        assertThat(bill.getExchangeRate()).isEqualByComparingTo(BigDecimal.valueOf(1.2));
    }

    @Test(expected = InvalidArgumentException.class)
    public void createInvalidBill_ShouldThrowInvalidArgumentExceptionAndReturnNull() throws PersistenceException, InvalidArgumentException {
        Bill bill = null;
        billDAO.createBill(bill);
    }

    @Test
    public void deleteExistingBill_ShouldReturnBillWithDeleteFlag() throws PersistenceException, InvalidArgumentException {
        billDAO.deleteBill(1);
        Bill bill = billDAO.findBillById(1);
        assertThat(bill.isDeleted()).isTrue();
    }

    @Test(expected = InvalidArgumentException.class)
    public void deleteBillNegativeID_ShouldThrowIllegalArgumentException() throws InvalidArgumentException, PersistenceException {
        billDAO.deleteBill(-1);
    }

    @Test
    public void undoDeleteOfBill_ReturnBillWithoutDeleteFlag() throws PersistenceException, InvalidArgumentException {
        assertThat(billDAO.undoDeleteBill(1)).isTrue();
        assertThat(billDAO.findBillById(1).isDeleted()).isFalse();
    }

    @Test(expected = InvalidArgumentException.class)
    public void undoDeleteOfNegativeBillID_throwInvalidArgumentException() throws InvalidArgumentException, PersistenceException {
        billDAO.undoDeleteBill(-1);
    }

    @Test
    public void editBillDescription_ShouldReturnEditedBill() throws PersistenceException, InvalidArgumentException {
        Bill bill = billDAO.findBillById(1);
        bill.setDescription("test edited description");
        bill = billDAO.editBill(bill);
        assertThat(bill.getDescription()).isEqualTo("test edited description");
    }

    @Test(expected = InvalidArgumentException.class)
    public void editInvalidBill_throwInvalidArgumentException() throws PersistenceException, InvalidArgumentException {
        billDAO.editBill(null);
    }

    @Test
    public void findBillsInGroup_ShouldReturnBillsOfGroupIDOnly() throws PersistenceException, InvalidArgumentException {
        BillQuery billQuery = new BillQuery();
        Group group = new Group();
        group.setId(1);
        billQuery.setGroup(group);
        List<Bill> bills = billDAO.findBillsInGroup(billQuery);
        bills.forEach(bill -> assertThat(bill.getGroup().getId()).isEqualTo(1));
    }

    @Test(expected = InvalidArgumentException.class)
    public void findBillsInGroup_ShouldThrowInvalidArgumentException() throws PersistenceException, InvalidArgumentException {
        billDAO.findBillsInGroup(null);
    }

    @Test
    public void findBillsInGroup_ShouldReturnBillsWithAmountBetweenMinMax() throws PersistenceException, InvalidArgumentException {
        BillQuery billQuery = new BillQuery();
        Group group = new Group();
        group.setId(1);
        billQuery.setGroup(group);
        billQuery.setMaxAmount(BigDecimal.valueOf(10500));
        billQuery.setMinAmount(BigDecimal.valueOf(100));
        List<Bill> bills = billDAO.findBillsInGroup(billQuery);
        bills.forEach(bill -> assertThat(bill.getAmount()).isBetween(BigDecimal.valueOf(100), BigDecimal.valueOf(10500)));
    }

    @Test(expected = InvalidArgumentException.class)
    public void findBillsInGroupWithInvalidQuery_throwInvalidArgumentException() throws PersistenceException, InvalidArgumentException {
        BillQuery billQuery = new BillQuery();
        billDAO.findBillsInGroup(billQuery);
    }

    @Test
    public void findBillsInGroup_ReturnOnlyDeletedBills() throws PersistenceException, InvalidArgumentException {
        BillQuery billQuery = new BillQuery();
        Group group = new Group();
        group.setId(1);
        billQuery.setGroup(group);
        billQuery.setDeleted(true);
        List<Bill> bills = billDAO.findBillsInGroup(billQuery);
        bills.forEach(bill -> assertThat(bill.isDeleted()).isTrue());
    }

    @Test
    public void findBillsInGroup_ReturnBillsWithSpecificDescription() throws PersistenceException, InvalidArgumentException {
        BillQuery billQuery = new BillQuery();
        Group group = new Group();
        group.setId(1);
        billQuery.setGroup(group);
        billQuery.setText("nd");
        List<Bill> bills = billDAO.findBillsInGroup(billQuery);
        bills.forEach(bill -> assertThat(bill.getDescription()).contains("nd"));
    }

    @Test
    public void findBillsInGroup_ReturnBillsFromUserOnly() throws PersistenceException, InvalidArgumentException {
        BillQuery billQuery = new BillQuery();
        Group group = new Group();
        group.setId(1);
        billQuery.setGroup(group);
        User user = new User();
        user.setId(1);
        billQuery.setPayer(user);
        List<Bill> bills = billDAO.findBillsInGroup(billQuery);
        bills.forEach(bill -> assertThat(bill.getUser().getId()).isEqualTo(1));
    }

    @Test
    public void findBillsInGroup_ReturnBillsWithTagOnly() throws PersistenceException, InvalidArgumentException {
        BillQuery billQuery = new BillQuery();
        Group group = new Group();
        group.setId(1);
        Tag tag = new Tag();
        tag.setId(1);
        billQuery.setGroup(group);
        billQuery.setTag(tag);
        List<Bill> bills = billDAO.findBillsInGroup(billQuery);
        bills.forEach(bill -> assertThat(bill.getTag().getName()).isEqualTo("Rent"));
    }
}