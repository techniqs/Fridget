package at.ac.tuwien.sepm.fridget.common.services;

import at.ac.tuwien.sepm.fridget.common.entities.Event;
import at.ac.tuwien.sepm.fridget.common.entities.User;
import at.ac.tuwien.sepm.fridget.common.exception.InvalidArgumentException;
import at.ac.tuwien.sepm.fridget.common.exception.PersistenceException;

import java.util.List;

public interface EventService {

    Event createEvent(Event event) throws PersistenceException, InvalidArgumentException;

    List<Event> findEvents(int groupId) throws PersistenceException, InvalidArgumentException;

    /**
     * Trigger event log synchronization for a user, i.e. get all events for groups this user is in
     *
     * @param limit how many events should be fetched
     * @throws PersistenceException
     * @throws InvalidArgumentException
     */
    List<Event> fetchRecentEvents(User user, int limit) throws PersistenceException, InvalidArgumentException;

    List<Event> checkForUpdates(User user) throws PersistenceException, InvalidArgumentException;

    List<Event> getEventLog();
}
