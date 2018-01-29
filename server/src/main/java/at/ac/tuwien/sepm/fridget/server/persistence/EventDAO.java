package at.ac.tuwien.sepm.fridget.server.persistence;

import at.ac.tuwien.sepm.fridget.common.entities.Event;
import at.ac.tuwien.sepm.fridget.common.entities.User;
import at.ac.tuwien.sepm.fridget.common.exception.InvalidArgumentException;
import at.ac.tuwien.sepm.fridget.common.exception.PersistenceException;

import java.util.List;

public interface EventDAO {

    Event createEvent(Event event) throws PersistenceException, InvalidArgumentException;

    List<Event> findAllEventsForGroup(int groupId) throws PersistenceException, InvalidArgumentException;

    List<Event> findEventsForEventLog(User user, int limit) throws PersistenceException, InvalidArgumentException;
}
