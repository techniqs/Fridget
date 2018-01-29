package at.ac.tuwien.sepm.fridget.server.services;

import at.ac.tuwien.sepm.fridget.common.entities.Event;
import at.ac.tuwien.sepm.fridget.common.entities.EventType;
import at.ac.tuwien.sepm.fridget.common.entities.User;
import at.ac.tuwien.sepm.fridget.common.exception.InvalidArgumentException;
import at.ac.tuwien.sepm.fridget.common.exception.PersistenceException;
import at.ac.tuwien.sepm.fridget.common.services.EventService;
import at.ac.tuwien.sepm.fridget.common.services.GroupService;
import at.ac.tuwien.sepm.fridget.server.persistence.EventDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ServerEventService implements EventService {

    @Autowired
    private EventDAO eventDAO;

    @Autowired
    private GroupService groupService;

    /**
     * How many of the last events should be fetched at startup
     */
    private final int EVENT_LIMIT = 15;

    private boolean firstFetch = true;
    private List<Event> eventCache = new ArrayList<>();

    @Override
    public Event createEvent(Event event) throws PersistenceException, InvalidArgumentException {
        validateEvent(event);
        event = eventDAO.createEvent(event);
        eventCache.add(0, event);
        return event;
    }

    @Override
    public List<Event> findEvents(int groupId) throws PersistenceException, InvalidArgumentException {
        if (groupId > 0) {
            return eventDAO.findAllEventsForGroup(groupId);
        }
        return null;
    }

    @Override
    public List<Event> fetchRecentEvents(User user, int limit) throws PersistenceException, InvalidArgumentException {
        if (user == null) throw new InvalidArgumentException("User must not be null!");
        List<Event> events = eventDAO.findEventsForEventLog(user, EVENT_LIMIT);
        if (firstFetch) {
            eventCache = events;
            firstFetch = false;
        }
        return events;
    }

    @Override
    public List<Event> checkForUpdates(User user) throws PersistenceException, InvalidArgumentException {
        List<Event> potentiallyNew = fetchRecentEvents(user, -1);
        potentiallyNew.removeIf(item -> eventCache.contains(item));
        // Now potentiallyNew contains only "really" new elements
        eventCache.addAll(potentiallyNew);
        return potentiallyNew;
    }

    /**
     * Dummy interface implementation
     *
     * @return
     */
    @Override
    public List<Event> getEventLog() {
        return null;
    }

    private void validateEvent(Event event) throws PersistenceException, InvalidArgumentException {
        if (event == null) throw new InvalidArgumentException("Event must not be null!");
        // Same for all events
        if (event.getType() == null) {
            throw new InvalidArgumentException("Event type must not be null!");
        }
        if (!event.getType().equals(EventType.INVITATION_ACCEPTED) && event.getSource() == null) {
            throw new InvalidArgumentException("Source user of event must not be null!");
        } else if (!event.getType().equals(EventType.INVITATION_ACCEPTED) && event.getSource().getId() < 1) {
            throw new InvalidArgumentException("Invalid source user!");
        }
        if (event.getOccuredAt() == null) {
            throw new InvalidArgumentException("Date and time of event must be set!");
        }
        if (event.getGroup() == null) {
            throw new InvalidArgumentException("Group must not be null!");
        } else if (event.getGroup().getId() < 1) {
            throw new InvalidArgumentException("Invalid group ID for event!");
        }
        // Check if user is in group
        if (!event.getType().equals(EventType.INVITATION_ACCEPTED) &&
            !groupService.hasMember(event.getGroup().getId(), event.getSource().getId())) {
            throw new InvalidArgumentException("Invoking member is not part of the group!");
        }
        // Split handling by invitation types
        switch (event.getType()) {

            case INVITATION_SENT:
                if (event.getTarget() == null) {
                    throw new InvalidArgumentException("Target user of event must not be null!");
                } else if (event.getTarget().getId() < 1) {
                    throw new InvalidArgumentException("Invalid target user!");
                }
                break;

            case INVITATION_ACCEPTED:
                if (event.getTarget() == null) {
                    throw new InvalidArgumentException("Target user of event must not be null!");
                } else if (event.getTarget().getId() < 1) {
                    throw new InvalidArgumentException("Invalid target user!");
                }
                break;

            case MEMBER_LEFT:

                break;
            case MODIFY_BILL:
                if (event.getBillBefore() == null) {
                    throw new InvalidArgumentException("Bill before must not be null!");
                } else if (event.getBillBefore().getId() < 1) {
                    throw new InvalidArgumentException("Invalid ID for bill before!");
                }
                if (event.getBillAfter() == null) {
                    throw new InvalidArgumentException("Bill after must not be null!");
                } else if (event.getBillAfter().getId() < 1) {
                    throw new InvalidArgumentException("Invalid ID for bill after!");
                }
                break;
            case CREATE_BILL:
                if (event.getBillBefore() != null) {
                    throw new InvalidArgumentException("Bill before must not be set!");
                }
                if (event.getBillAfter() == null) {
                    throw new InvalidArgumentException("Bill after must not be null!");
                } else if (event.getBillAfter().getId() < 1) {
                    throw new InvalidArgumentException("Invalid ID for bill after!");
                }
        }
    }

}
