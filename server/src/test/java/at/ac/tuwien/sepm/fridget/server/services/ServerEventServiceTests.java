package at.ac.tuwien.sepm.fridget.server.services;

import at.ac.tuwien.sepm.fridget.common.entities.Event;
import at.ac.tuwien.sepm.fridget.common.entities.EventType;
import at.ac.tuwien.sepm.fridget.common.exception.InvalidArgumentException;
import at.ac.tuwien.sepm.fridget.common.exception.PersistenceException;
import at.ac.tuwien.sepm.fridget.common.services.EventService;
import at.ac.tuwien.sepm.fridget.server.TestBase;
import at.ac.tuwien.sepm.fridget.server.persistence.EventDAO;
import at.ac.tuwien.sepm.fridget.server.util.TestUtil;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class ServerEventServiceTests extends TestBase {

    @Autowired
    EventDAO eventDAO;

    @Autowired
    EventService eventService;

    @Autowired
    TestUtil testUtil;

    private Event event;

    /* INVITATION SENT */
    @Test
    public void createValidInvitationSentEvent_ShouldReturnEventWithID() throws InvalidArgumentException, PersistenceException {
        event = eventService.createEvent(testUtil.createValidInvitationSentEvent());
        assertThat(event).isNotNull();
        assertThat(event.getId()).isGreaterThan(0);
        assertThat(event.getType().equals(EventType.INVITATION_SENT));
        assertThat(event.isUndone()).isEqualTo(false);
        assertThat(event.getSource()).isNotNull();
        assertThat(event.getGroup().getMembers().contains(event.getSource().getId()));
        assertThat(event.getTarget()).isNotNull();
        assertThat(event.getGroup().getMembers()).contains(event.getTarget().getId());
        assertThat(event.getDescription()).isNotNull();
        assertThat(event.getGroup()).isNotNull();
        assertThat(event.getOccuredAt()).isNotNull();
    }

    @Test(expected = InvalidArgumentException.class)
    public void createInvalidInvitationSentEvent_ThrowInvalidArgumentException() throws InvalidArgumentException, PersistenceException {
        event = testUtil.createValidInvitationSentEvent();
        event.setTarget(null);
        eventService.createEvent(event);
    }

    /* INVITATION ACCEPTED */
    @Test
    public void createValidInvitationAcceptedEvent_ShouldReturnEventWithID() throws InvalidArgumentException, PersistenceException {
        event = testUtil.createValidInvitationAcceptedEvent();
        event = eventService.createEvent(event);
        assertThat(event).isNotNull();
        assertThat(event.getId()).isGreaterThan(0);
        assertThat(event.getType().equals(EventType.INVITATION_ACCEPTED));
        assertThat(event.isUndone()).isEqualTo(false);
        assertThat(event.getSource()).isNotNull();
        assertThat(event.getGroup().getMembers().contains(event.getSource().getId()));
        assertThat(event.getTarget()).isNotNull();
        assertThat(event.getGroup().getMembers()).contains(event.getTarget().getId());
        assertThat(event.getBillBefore()).isNull();
        assertThat(event.getBillAfter()).isNull();
        assertThat(event.getDescription()).isNotNull();
        assertThat(event.getGroup()).isNotNull();
        assertThat(event.getOccuredAt()).isNotNull();
    }

    @Test(expected = InvalidArgumentException.class)
    public void createInvalidInvitationAcceptedEvent_ThrowInvalidArgumentException() throws InvalidArgumentException, PersistenceException {
        event = testUtil.createValidInvitationAcceptedEvent();
        event.setTarget(null);
        eventService.createEvent(event);
    }

    /* MEMBER LEFT */
    @Test
    public void createValidMemberLeftEvent_ShouldReturnEventWithID() throws InvalidArgumentException, PersistenceException {
        event = testUtil.createValidMemberLeftEvent();
        event = eventService.createEvent(event);
        assertThat(event).isNotNull();
        assertThat(event.getId()).isGreaterThan(0);
        assertThat(event.getType().equals(EventType.MEMBER_LEFT));
        assertThat(event.isUndone()).isEqualTo(false);
        assertThat(event.getSource()).isNotNull();
        assertThat(event.getDescription()).isNotNull();
        assertThat(event.getOccuredAt()).isNotNull();
    }

    @Test(expected = InvalidArgumentException.class)
    public void createInvalidMemberLeftEvent_ThrowInvalidArgumentException() throws InvalidArgumentException, PersistenceException {
        event = testUtil.createValidMemberLeftEvent();
        event.setGroup(null);
        eventService.createEvent(event);
    }

    /* MODIFY BILL */
    @Test
    public void createValidModifyBillEvent_ShouldReturnEventWithID() throws InvalidArgumentException, PersistenceException {
        event = testUtil.createValidModifyBillEvent();
        event = eventService.createEvent(event);
        assertThat(event).isNotNull();
        assertThat(event.getId()).isGreaterThan(0);
        assertThat(event.getType().equals(EventType.MODIFY_BILL));
        assertThat(event.isUndone()).isEqualTo(false);
        assertThat(event.getSource()).isNotNull();
        assertThat(event.getDescription()).isNotNull();
        assertThat(event.getOccuredAt()).isNotNull();
    }

    @Test(expected = InvalidArgumentException.class)
    public void createInvalidModifyBillEvent_ThrowInvalidArgumentException() throws InvalidArgumentException, PersistenceException {
        event = testUtil.createValidModifyBillEvent();
        event.setBillAfter(null);
        eventService.createEvent(event);
    }

    /* CREATE BILL */
    @Test
    public void createValidCreateBillEvent_ShouldReturnEventWithID() throws InvalidArgumentException, PersistenceException {
        event = testUtil.createValidCreateBillEvent();
        event = eventService.createEvent(event);
        assertThat(event).isNotNull();
        assertThat(event.getId()).isGreaterThan(0);
        assertThat(event.getType().equals(EventType.CREATE_BILL));
        assertThat(event.isUndone()).isEqualTo(false);
        assertThat(event.getSource()).isNotNull();
        assertThat(event.getDescription()).isNotNull();
        assertThat(event.getOccuredAt()).isNotNull();
    }

    @Test(expected = InvalidArgumentException.class)
    public void createInvalidCreateBillEvent_ThrowInvalidArgumentException() throws InvalidArgumentException, PersistenceException {
        event = testUtil.createValidCreateBillEvent();
        event.setBillAfter(null);
        eventService.createEvent(event);
    }
}
