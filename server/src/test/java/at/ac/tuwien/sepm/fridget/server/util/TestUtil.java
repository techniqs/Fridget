package at.ac.tuwien.sepm.fridget.server.util;

import at.ac.tuwien.sepm.fridget.common.entities.*;
import at.ac.tuwien.sepm.fridget.common.exception.InvalidArgumentException;
import at.ac.tuwien.sepm.fridget.common.exception.PersistenceException;
import at.ac.tuwien.sepm.fridget.common.services.GroupService;
import at.ac.tuwien.sepm.fridget.common.services.UserService;
import at.ac.tuwien.sepm.fridget.common.util.Currency;
import at.ac.tuwien.sepm.fridget.common.util.ShareTechniqueId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Convenience class for creating test objects
 */
@Component
public class TestUtil {

    @Autowired
    GroupService groupService;

    @Autowired
    UserService userService;

    /**
     * Convenience method for creating a valid bill
     *
     * @return valid bill
     */
    public Bill createValidBill() {
        Bill bill = new Bill();
        bill.setAmount(BigDecimal.TEN);
        bill.setShareTechniqueId(ShareTechniqueId.EVEN);
        bill.setTitle("TestGroceries");
        bill.setDescription("Christmas food");
        bill.setCreatedAt(LocalDateTime.now());
        bill.setUpdatedAt(LocalDateTime.now());
        bill.setDeleted(false);
        bill.setCurrency(Currency.EUR);
        bill.setExchangeRate(BigDecimal.valueOf(1.2));
        bill.setUser(new User(2, "Bob", "qsefridget+bob@gmail.com", "fridgetbob"));
        bill.setGroup(new Group(1, "boys flat sharing", Currency.EUR.getId()));
        bill.setBillShares(Arrays.asList(
            new BillShare(5, new User(2, "Bob", "qsefridget+bob@gmail.com", "fridgetbob"), BigDecimal.ZERO),
            new BillShare(5, new User(5, "Erik", "qsefridget+erik@gmail.com", "fridgeterik"), BigDecimal.ZERO)
        ));
        return bill;
    }

    /**
     * Convenience method for creating an invalid bill
     *
     * @return the invalid bill
     */
    public Bill createInvalidBill() {
        Bill bill = new Bill();
        bill.setAmount(BigDecimal.ZERO);
        bill.setShareTechniqueId(ShareTechniqueId.EVEN);
        bill.setTitle("TestGroceries");
        bill.setDescription("Christmas food");
        bill.setCreatedAt(LocalDateTime.now());
        bill.setUpdatedAt(LocalDateTime.now());
        bill.setDeleted(false);
        bill.setCurrency(Currency.EUR);
        bill.setExchangeRate(BigDecimal.valueOf(1.2));
        return bill;
    }

    // Dummy Groups and Users
    public Group createValidGroup() throws InvalidArgumentException {
        Group group = new Group();
        for (int i = 1; i <= 5; i++) {
            User user = new User();
            user.setEmail("testmail@test.com");
            user.setName("TestName");
            user.setPassword("TestPassword");
            user.setId(i);
            group.addMembers(user.getId());
        }
        group.setName("Testgroup");

        return group;
    }

    public Group createDbValidGroup() throws InvalidArgumentException, PersistenceException {

        Group group = new Group();
        group.setName("Group");
        group = groupService.createGroup(group, 1);

        return group;
    }

    public BillShare createValidBillShare() {
        BillShare share = new BillShare();
        share.setUser(new User(2, "Bob", "qsefridget+bob@gmail.com", "fridgetbob"));
        share.setAmount(BigDecimal.TEN);
        return share;
    }

    public Tag createValidTag() {
        Tag tag = new Tag();
        tag.setName("Test Tag");
        return tag;
    }

    private Event createValidFilledEvent() throws InvalidArgumentException, PersistenceException {
        User source = new User();
        source.setEmail("qsefridget+erik@gmail.com");
        source.setName("Erik");
        source.setPassword("fridgeterik");
        source.setId(5);

        User target = new User();
        target.setEmail("qsefridget+bob@gmail.com");
        target.setName("Bob");
        target.setPassword("fridgetbob");
        target.setId(2);

        Bill billBefore = new Bill();
        billBefore.setId(1);
        billBefore.setAmount(BigDecimal.valueOf(1000));
        billBefore.setShareTechniqueId(ShareTechniqueId.fromId(1));
        billBefore.setTitle("Groceries");
        billBefore.setDescription("for the weekend");
        billBefore.setCreatedAt(LocalDateTime.now());
        billBefore.setUpdatedAt(LocalDateTime.now());
        billBefore.setDeleted(false);
        billBefore.setCurrency(Currency.fromId(1));
        billBefore.setExchangeRate(BigDecimal.valueOf(10000));
        billBefore.setUser(userService.findUserById(2));
        billBefore.setGroup(groupService.getGroup(1));
        Tag tag = new Tag();
        tag.setId(1);
        billBefore.setTag(tag);

        Bill billAfter = new Bill();
        billAfter.setId(1);
        billAfter.setAmount(billBefore.getAmount().add(BigDecimal.TEN));
        billAfter.setShareTechniqueId(ShareTechniqueId.fromId(1));
        billAfter.setTitle("Groceries");
        billAfter.setDescription("ASDFASDF");
        billAfter.setCreatedAt(LocalDateTime.now());
        billAfter.setUpdatedAt(LocalDateTime.now());
        billAfter.setDeleted(false);
        billAfter.setCurrency(Currency.fromId(1));
        billAfter.setExchangeRate(BigDecimal.valueOf(10000));
        billAfter.setUser(userService.findUserById(2));
        billAfter.setGroup(groupService.getGroup(1));
        billAfter.setTag(tag);

        Group group = new Group();
        group.setId(1);
        group.setName("boys flat sharing");
        group.setBaseCurrencyID(1);
        List<Integer> members = new ArrayList<>();
        members.add(2);
        members.add(5);
        group.setMembers(members);

        return new Event(EventType.INVITATION_SENT,
            false,
            LocalDateTime.now(),
            "ASDF",
            source,
            target,
            billAfter,
            billAfter,
            group);

    }

    public Event createValidInvitationSentEvent() throws InvalidArgumentException, PersistenceException {
        Event valid = createValidFilledEvent();
        valid.setBillBefore(null);
        valid.setBillAfter(null);
        return valid;
    }

    public Event createValidInvitationAcceptedEvent() throws InvalidArgumentException, PersistenceException {
        Event valid = createValidFilledEvent();
        valid.setType(EventType.INVITATION_ACCEPTED);
        valid.setBillBefore(null);
        valid.setBillAfter(null);
        return valid;
    }

    public Event createValidMemberLeftEvent() throws InvalidArgumentException, PersistenceException {
        Event valid = createValidFilledEvent();
        valid.setType(EventType.MEMBER_LEFT);
        valid.setBillBefore(null);
        valid.setBillAfter(null);
        valid.setTarget(null);
        return valid;
    }

    public Event createValidModifyBillEvent() throws InvalidArgumentException, PersistenceException {
        Event valid = createValidFilledEvent();
        valid.setType(EventType.MODIFY_BILL);
        valid.setTarget(null);
        return valid;
    }

    public Event createValidCreateBillEvent() throws InvalidArgumentException, PersistenceException {
        Event valid = createValidFilledEvent();
        valid.setType(EventType.CREATE_BILL);
        valid.setBillBefore(null);
        valid.setTarget(null);
        return valid;
    }

    public String getRandomEmail() {
        return "qsefridget+userdaoh2tests" + getRandomString() + "@gmail.com";
    }

    public String getRandomString() {
        return UUID.randomUUID().toString().replace("-", "");
    }

}
