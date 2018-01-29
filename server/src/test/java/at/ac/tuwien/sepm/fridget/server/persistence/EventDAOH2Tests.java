package at.ac.tuwien.sepm.fridget.server.persistence;

import at.ac.tuwien.sepm.fridget.common.entities.Event;
import at.ac.tuwien.sepm.fridget.common.entities.EventType;
import at.ac.tuwien.sepm.fridget.common.exception.InvalidArgumentException;
import at.ac.tuwien.sepm.fridget.common.exception.PersistenceException;
import at.ac.tuwien.sepm.fridget.server.TestBase;
import at.ac.tuwien.sepm.fridget.server.util.TestUtil;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class EventDAOH2Tests extends TestBase {
    @Autowired
    EventDAO eventDAO;

    @Autowired
    TestUtil testUtil;

    private Event event;

    @Test
    public void createValidEvent_ShouldReturnEventWithID() throws PersistenceException, InvalidArgumentException {
        event = testUtil.createValidInvitationSentEvent();
        eventDAO.createEvent(event);
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

    @Test(expected = PersistenceException.class)
    public void createNotInvitationAcceptedEventWithoutSource_ThrowInvalidArgumentException() throws PersistenceException, InvalidArgumentException {
        event = testUtil.createValidInvitationSentEvent();
        event.setSource(null);
        eventDAO.createEvent(event);
    }

    @Test(expected = PersistenceException.class)
    public void createEventWithoutType_ThrowInvalidArgumentException() throws PersistenceException, InvalidArgumentException {
        event = testUtil.createValidInvitationSentEvent();
        event.setType(null);
        eventDAO.createEvent(event);
    }

    @Test(expected = PersistenceException.class)
    public void createEventWithoutDate_ThrowInvalidArgumentException() throws PersistenceException, InvalidArgumentException {
        event = testUtil.createValidInvitationSentEvent();
        event.setOccuredAt(null);
        eventDAO.createEvent(event);
    }

    @Test(expected = PersistenceException.class)
    public void createEventWithoutGroup_ThrowInvalidArgumentException() throws PersistenceException, InvalidArgumentException {
        event = testUtil.createValidInvitationSentEvent();
        event.setGroup(null);
        eventDAO.createEvent(event);
    }
}
